package com.afyaquik.utils.otherservices.impl;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.utils.mappers.MapperRegistry;
import com.afyaquik.utils.otherservices.EntityUtilService;
import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityUtilServiceImpl implements EntityUtilService {

    @PersistenceContext
    private EntityManager entityManager;

    private final MapperRegistry mapperRegistry;

    private final Map<String, Join<?, ?>> joins = new HashMap<>();


    @Override
    @Cacheable(value = "searchResults", key = "#searchDto.searchEntity + '-' + #searchDto.query + '-' + #searchDto.page + '-' + #searchDto.size + '-' + #searchDto.sort+ '-' + #searchDto.dateFilter")
    public SearchResponseDto search(SearchDto searchDto) {
        String entityKey = searchDto.getSearchEntity();
        if (entityKey == null) throw new IllegalArgumentException("Search entity not specified");

        try {
            Class<?> entityClass = resolveEntityClass(entityKey);
            Class<?> dtoClass = resolveDtoClass(entityKey);
            EntityMapper<Object, Object> mapper = mapperRegistry.getMapper(entityKey);
            if (mapper == null) throw new IllegalArgumentException("No mapper registered for entity: " + entityKey);

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Main query
            CriteriaQuery<Object> query = cb.createQuery();
            Root<?> root = query.from(entityClass);
            query.select(root);

            Predicate predicate = buildPredicates(searchDto, cb, root, entityClass);
            if (predicate!=null) {
                query.where(predicate);
            }

            Pageable pageable;
            if (searchDto.getSort() != null && !searchDto.getSort().isBlank()) {
                String[] sortParts = searchDto.getSort().split(",");
                if (sortParts.length == 2) {
                    String sortField = sortParts[0].trim();
                    String sortDirection = sortParts[1].trim().toLowerCase();
                    Sort sort = sortDirection.equals("desc")
                            ? Sort.by(Sort.Order.desc(sortField))
                            : Sort.by(Sort.Order.asc(sortField));

                    pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
                } else {
                    pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
                }
            } else {
                pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
            }


            // Sorting
            if (pageable.getSort().isSorted()) {
                List<Order> orders = pageable.getSort().stream()
                        .map(order -> order.isAscending() ? cb.asc(resolveJoinPath(root,order.getProperty(),entityClass)) : cb.desc(resolveJoinPath(root,order.getProperty(),entityClass)))
                        .collect(Collectors.toList());
                query.orderBy(orders);
            }

            // Execute main query
            List<?> entities = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            List<?> dtos = entities.stream().map(mapper::toDto).collect(Collectors.toList());

            // Count query
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(entityClass);
            countQuery.select(cb.count(countRoot));
            Predicate countPredicates = buildPredicates(searchDto, cb, countRoot, entityClass);

            if (countPredicates!=null) {
                countQuery.where(countPredicates);
            }

            long total = entityManager.createQuery(countQuery).getSingleResult();
            Page<?> page = new PageImpl<>(dtos, pageable, total);

            return new SearchResponseDto(page, searchDto.getSearchFields(), searchDto.getQuery());

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity class not found for: " + searchDto.getSearchEntity(), e);
        }
    }

    private Object mapToDto(Object entity, String entityName) {
        EntityMapper<Object, Object> mapper = mapperRegistry.getMapper(entityName);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper registered for entity: " + entityName);
        }
        return mapper.toDto(entity);
    }

    private Path<?> resolveJoinPath(Root<?> root, String fieldPath, Class<?> entityClass) {
        String[] parts = fieldPath.split("\\.");
        From<?, ?> current = root;
        Class<?> currentClass = entityClass;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Field field;

            try {
                field = currentClass.getDeclaredField(part);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + part);
            }

            boolean isCollection = Collection.class.isAssignableFrom(field.getType());

            // This works because JPA will use SetJoin internally
            current = ((From<?, ?>) current).join(part, JoinType.LEFT);

            // Handle generic type of collection
            if (isCollection) {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                currentClass = (Class<?>) pt.getActualTypeArguments()[0];
            } else {
                currentClass = field.getType();
            }
        }

        return current.get(parts[parts.length - 1]);
    }
    private LocalDate parseDateToLocalDate(String input) {
        try {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    private Predicate buildPredicates(SearchDto dto, CriteriaBuilder cb, Root<?> root, Class<?> entityClass) {
        List<Predicate> predicates = new ArrayList<>();
        if (dto.getSearchFields() == null) dto.setSearchFields(new ArrayList<>());

        // Handle date filter
        if (dto.getDateFilter() != null && !dto.getDateFilter().isEmpty()) {
            String[] parts = dto.getDateFilter().split("#");
            if (parts.length == 2) {
                String fieldPath = parts[0];
                String fieldValue = parts[1];

                Path<?> path = resolveJoinPath(root, fieldPath, entityClass);
                LocalDate parsedDate = parseDateToLocalDate(fieldValue);
                if (parsedDate != null) {
                    predicates.add(cb.between(
                            path.as(LocalDateTime.class),
                            parsedDate.atStartOfDay(),
                            parsedDate.plusDays(1).atStartOfDay()
                    ));
                }
            } else {
                throw new IllegalArgumentException("Invalid date filter format: " + dto.getDateFilter());
            }
        }

        // Handle search terms
        if (dto.getQuery() != null && !dto.getQuery().isEmpty()) {

            String[] searchTerms = dto.getQuery().split(",");
            List<Predicate> termPredicates = new ArrayList<>();

            for (String term : searchTerms) {
                term = term.trim();
                List<Predicate> fieldPredicates = new ArrayList<>();
                if (!term.contains("=")) {
                    term= term.toLowerCase(Locale.ROOT);
                    for (String field : dto.getSearchFields()) {
                        try {
                            Path<?> path = resolveJoinPath(root, field, entityClass);
                            Class<?> type = path.getJavaType();

                            if (type.equals(String.class)) {
                                switch (dto.getOperator()) {
                                    case "like" ->
                                            fieldPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + term + "%"));
                                    case "equals" ->
                                            fieldPredicates.add(cb.equal(cb.lower(path.as(String.class)), term));
                                    case "startsWith" ->
                                            fieldPredicates.add(cb.like(cb.lower(path.as(String.class)), term + "%"));
                                    case "endsWith" ->
                                            fieldPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + term));
                                    default ->
                                            fieldPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + term + "%"));
                                }

                            } else if (type.equals(LocalDateTime.class)) {
                                LocalDateTime parsed = parseDate(term);
                                if (parsed != null)
                                    fieldPredicates.add(cb.equal(path.as(LocalDateTime.class), parsed));
                            } else if (type.equals(Long.class)) {
                                fieldPredicates.add(cb.equal(path.as(Long.class), Long.parseLong(term)));
                            } else {
                                fieldPredicates.add(cb.equal(path.as(String.class), term));
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid search field: {}", field);
                        }
                    }
                    if (!fieldPredicates.isEmpty()) {
                        termPredicates.add(cb.or(fieldPredicates.toArray(new Predicate[0])));
                    }
                }
                else
                {
                    List<Predicate> extraTermPredicates = new ArrayList<>();

                    try {
                        String[] parts = term.split("=");
                        String field = parts[0].trim();
                        String value = parts[1].trim();

                        Path<?> path = resolveJoinPath(root, field, entityClass);
                        Class<?> type = path.getJavaType();

                        // Check if the value contains || for OR operation
                        if (value.contains("||")) {
                            String[] orValues = value.split("\\|\\|");
                            List<Predicate> orPredicates = new ArrayList<>();

                            for (String orValue : orValues) {
                                orValue = orValue.trim();
                                if (type.equals(String.class)) {
                                    orPredicates.add(cb.equal(cb.lower(path.as(String.class)), orValue.toLowerCase(Locale.ROOT)));
                                } else if (type.equals(LocalDateTime.class)) {
                                    LocalDateTime parsed = parseDate(orValue);
                                    if (parsed != null) {
                                        orPredicates.add(cb.equal(path.as(LocalDateTime.class), parsed));
                                    }
                                } else if (type.equals(Long.class)) {
                                    orPredicates.add(cb.equal(path.as(Long.class), Long.parseLong(orValue)));
                                } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                                    orPredicates.add(cb.equal(path.as(Boolean.class), Boolean.parseBoolean(orValue)));
                                } else {
                                    orPredicates.add(cb.equal(path.as(String.class), orValue));
                                }
                            }

                            if (!orPredicates.isEmpty()) {
                                extraTermPredicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
                            }
                        } else {
                            // logic for single value
                            if (type.equals(String.class)) {
                                extraTermPredicates.add(cb.equal(cb.lower(path.as(String.class)), value.toLowerCase(Locale.ROOT)));
                            } else if (type.equals(LocalDateTime.class)) {
                                LocalDateTime parsed = parseDate(value);
                                if (parsed != null)
                                    extraTermPredicates.add(cb.equal(path.as(LocalDateTime.class), parsed));
                            } else if (type.equals(Long.class)) {
                                extraTermPredicates.add(cb.equal(path.as(Long.class), Long.parseLong(value)));
                            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                                extraTermPredicates.add(cb.equal(path.as(Boolean.class), Boolean.parseBoolean(value)));
                            } else {
                                extraTermPredicates.add(cb.equal(path.as(String.class), value)); // fallback
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid search term: {}", term);
                    }
                    if (!extraTermPredicates.isEmpty()) {
                            // If we have multiple conditions, combine them with AND
                            termPredicates.add(cb.and(extraTermPredicates.toArray(new Predicate[0])));

                    }

                }
            }

            predicates.addAll(termPredicates);
        }

        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
    }

    private Class<?> resolveEntityClass(String key) throws ClassNotFoundException {
        return switch (key) {
            case "users" -> Class.forName("com.afyaquik.users.entity.User");
            case "roles" -> Class.forName("com.afyaquik.users.entity.Role");
            case "stations" -> Class.forName("com.afyaquik.users.entity.Station");
            case "patients" -> Class.forName("com.afyaquik.patients.entity.Patient");
            case "appointments" -> Class.forName("com.afyaquik.appointments.entity.Appointment");
            case "triageItems" -> Class.forName("com.afyaquik.patients.entity.TriageItem");
            case "visits" -> Class.forName("com.afyaquik.patients.entity.PatientVisit");
            case "generalSettings" -> Class.forName("com.afyaquik.utils.settings.entity.GeneralSettings");
            case "observationItems" -> Class.forName("com.afyaquik.doctor.entity.ObservationItem");
            case "observationItemCategories" -> Class.forName("com.afyaquik.doctor.entity.ObservationItemCategory");
            case "treatmentPlans" -> Class.forName("com.afyaquik.doctor.entity.TreatmentPlan");
            case "treatmentPlanItems" -> Class.forName("com.afyaquik.doctor.entity.TreatmentPlanItem");
            case "drugs" -> Class.forName("com.afyaquik.pharmacy.entity.Drug");
            case "drugInventory" -> Class.forName("com.afyaquik.pharmacy.entity.DrugInventory");
            case "drugCategories" -> Class.forName("com.afyaquik.pharmacy.entity.DrugCategory");
            case "drugForms" -> Class.forName("com.afyaquik.pharmacy.entity.DrugForm");
            case "patientAssignments" -> Class.forName("com.afyaquik.patients.entity.PatientAssignment");
            case "patientDrugs" -> Class.forName("com.afyaquik.pharmacy.entity.PatientDrug");
            case "apiPermissions" -> Class.forName("com.afyaquik.users.entity.security.ApiPermission");
            case "billingItems" -> Class.forName("com.afyaquik.billing.entity.BillingItem");
            case "billings" -> Class.forName("com.afyaquik.billing.entity.Billing");
            case "billingDetails" -> Class.forName("com.afyaquik.billing.entity.BillingDetail");
            case "currencies" -> Class.forName("com.afyaquik.billing.entity.Currency");
            default -> throw new ClassNotFoundException("No entity class for " + key);
        };
    }

    private Class<?> resolveDtoClass(String key) throws ClassNotFoundException {
        return switch (key) {
            case "users" -> Class.forName("com.afyaquik.users.dto.UserDto");
            case "roles" -> Class.forName("com.afyaquik.users.dto.RoleResponse");
            case "stations" -> Class.forName("com.afyaquik.users.dto.StationDto");
            case "patients" -> Class.forName("com.afyaquik.patients.dto.PatientDto");
            case "appointments" -> Class.forName("com.afyaquik.appointments.dto.AppointmentDto");
            case "triageItems" -> Class.forName("com.afyaquik.patients.dto.TriageItemDto");
            case "visits" -> Class.forName("com.afyaquik.patients.dto.PatientVisitDto");
            case "generalSettings" -> Class.forName("com.afyaquik.dtos.settings.GeneralSettingsDto");
            case "observationItems" -> Class.forName("com.afyaquik.doctor.dto.ObservationItemDto");
            case "observationItemCategories" -> Class.forName("com.afyaquik.doctor.dto.ObservationItemCategoryDto");
            case "treatmentPlans" -> Class.forName("com.afyaquik.doctor.dto.TreatmentPlanDto");
            case "treatmentPlanItems" -> Class.forName("com.afyaquik.doctor.dto.TreatmentPlanItemDto");
            case "drugs" -> Class.forName("com.afyaquik.pharmacy.dto.DrugDto");
            case "drugInventory" -> Class.forName("com.afyaquik.pharmacy.dto.DrugInventoryDto");
            case "drugCategories" -> Class.forName("com.afyaquik.pharmacy.dto.DrugCategoryDto");
            case "drugForms" -> Class.forName("com.afyaquik.pharmacy.dto.DrugFormDto");
            case "patientAssignments" -> Class.forName("com.afyaquik.patients.dto.PatientAssignmentDto");
            case "patientDrugs" -> Class.forName("com.afyaquik.pharmacy.dto.PatientDrugDto");
            case "apiPermissions" -> Class.forName("com.afyaquik.users.dto.security.ApiPermissionDto");
            case "billingItems" -> Class.forName("com.afyaquik.billing.dto.BillingItemDto");
            case "billings" -> Class.forName("com.afyaquik.billing.dto.BillingDto");
            case "billingDetails" -> Class.forName("com.afyaquik.billing.dto.BillingDetailDto");
            case "currencies" -> Class.forName("com.afyaquik.billing.dto.CurrencyDto");
            default -> throw new ClassNotFoundException("No DTO class for " + key);
        };
    }


    private LocalDateTime parseDate(String input) {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Path<String> resolvePath(Root<?> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) path = path.get(part);
        return (Path<String>) path;
    }

    @Override
    @Transactional
    //Rename it this to use in clearing cache after delete
    public void softdelete(String entityName, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        try {
            Class<Object> entityClass =(Class<Object>) resolveEntityClass(entityName);

            // Check if the entity or its superclasses have a 'deleted' property
            boolean hasDeletedField = false;
            Class<?> currentClass = entityClass;
            while (currentClass != null && !hasDeletedField) {
                try {
                    currentClass.getDeclaredField("deleted");
                    hasDeletedField = true;
                } catch (NoSuchFieldException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }

            if (!hasDeletedField) {
                throw new IllegalArgumentException("Entity " + entityName +
                        " and its superclasses do not have a 'deleted' property");
            }

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Object> update = cb.createCriteriaUpdate(entityClass);
            Root<?> root = update.from(entityClass);

            // Set deleted = true
            update.set("deleted", true);

            // Add WHERE clause for IDs
            update.where(root.get("id").in(ids));

            // Execute the update
            entityManager.createQuery(update).executeUpdate();

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity class not found for: " + entityName, e);
        }
    }
}
