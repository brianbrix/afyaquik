package com.afyaquik.utils.otherservices.impl;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.utils.mappers.MapperRegistry;
import com.afyaquik.utils.otherservices.SearchService;
import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    @PersistenceContext
    private EntityManager entityManager;

    private final MapperRegistry mapperRegistry;

    @Override
    @Cacheable("searchResults")
    public SearchResponseDto search(SearchDto searchDto, Pageable pageable) {
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

            // Sorting
            if (pageable.getSort().isSorted()) {
                List<Order> orders = pageable.getSort().stream()
                        .map(order -> order.isAscending() ? cb.asc(root.get(order.getProperty())) : cb.desc(root.get(order.getProperty())))
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
        if (dto.getSearchFields()==null) dto.setSearchFields(new ArrayList<>());
        if (dto.getDateFilter() != null && !dto.getDateFilter().isEmpty()) {
            String fieldPath = dto.getDateFilter().split("#")[0];
            String fieldValue = dto.getDateFilter().split("#")[1];
            if (fieldPath == null || fieldPath.isEmpty()) {
                throw new IllegalArgumentException("Invalid date filter format: " + dto.getDateFilter());
            }
            Path<?> path = resolveJoinPath(root, fieldPath, entityClass);
                LocalDate parsedDate = parseDateToLocalDate(fieldValue);
                if (parsedDate != null) {
                    // Match any time within that date
                    predicates.add(cb.between(
                            path.as(LocalDateTime.class),
                            parsedDate.atStartOfDay(),
                            parsedDate.plusDays(1).atStartOfDay()
                    ));

            }
        }
        if (dto.getQuery() != null && !dto.getQuery().isEmpty() &&
                dto.getSearchFields() != null && !dto.getSearchFields().isEmpty()) {


            String searchTerm = dto.getQuery().toLowerCase(Locale.ROOT);
            List<Predicate> queryPredicates = new ArrayList<>();

            for (String field : dto.getSearchFields()) {
                try {
                    Path<?> path = resolveJoinPath(root, field, entityClass);
                    Class<?> type = path.getJavaType();

                    if (type.equals(String.class)) {
                        queryPredicates.add(cb.like(cb.lower(path.as(String.class)), "%" + searchTerm + "%"));
                    } else if (type.equals(LocalDateTime.class)) {
                        LocalDateTime parsed = parseDate(searchTerm);
                        if (parsed != null) queryPredicates.add(cb.equal(path.as(LocalDateTime.class), parsed));

                    } else {
                        queryPredicates.add(cb.equal(path.as(String.class), searchTerm)); // fallback
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid search field: {}", field);
                }
            }
            if (!queryPredicates.isEmpty()) {
                predicates.add(cb.or(queryPredicates.toArray(new Predicate[0])));
            }
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
}
