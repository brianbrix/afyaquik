package com.afyaquik.core.settings.services.impl;

import com.afyaquik.core.mappers.EntityMapper;
import com.afyaquik.core.mappers.MapperRegistry;
import com.afyaquik.core.settings.services.SearchService;
import com.afyaquik.dtos.search.SearchDto;
import com.afyaquik.dtos.search.SearchResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
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
    public SearchResponseDto search(SearchDto searchDto, Pageable pageable) {
        try {
            Class<?> entityClass = null;
            Class<?> responseType = null;

            if (searchDto.getSearchEntity() != null) {
                responseType = switch (searchDto.getSearchEntity()) {
                    case "users" -> {
                        entityClass = Class.forName("com.afyaquik.users.entity.User");
                        yield Class.forName("com.afyaquik.dtos.user.UserDto");
                    }
                    case "roles" -> {
                        entityClass = Class.forName("com.afyaquik.users.entity.Role");
                        yield Class.forName("com.afyaquik.dtos.user.RoleResponse");
                    }
                    case "stations" -> {
                        entityClass = Class.forName("com.afyaquik.users.entity.Station");
                        yield Class.forName("com.afyaquik.dtos.user.StationDto");
                    }
                    case "patients" -> {
                        entityClass = Class.forName("com.afyaquik.patients.entity.Patient");
                        yield Class.forName("com.afyaquik.dtos.patient.PatientDto");
                    }
                    case "visits" -> {
                        entityClass = Class.forName("com.afyaquik.patients.entity.PatientVisit");
                        yield Class.forName("com.afyaquik.dtos.patient.PatientVisitDto");
                    }
                    case "settings" -> {
                        entityClass = Class.forName("com.afyaquik.core.settings.entity.GeneralSettings");
                        yield Class.forName("com.afyaquik.dtos.settings.GeneralSettingsDto");
                    }
                    default ->
                            throw new IllegalArgumentException("Unknown search entity: " + searchDto.getSearchEntity());
                };
            } else {
                throw new IllegalArgumentException("Search entity not specified");
            }

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Object> query = cb.createQuery();
            Root<?> root = query.from(entityClass);
            query.select(root);

            List<Predicate> predicates = buildPredicates(searchDto, cb, root);
            if (!predicates.isEmpty()) {
                query.where(cb.or(predicates.toArray(new Predicate[0])));
            }

            // Sorting
            if (pageable.getSort().isSorted()) {
                List<Order> orders = pageable.getSort().stream()
                        .map(order -> order.isAscending()
                                ? cb.asc(root.get(order.getProperty()))
                                : cb.desc(root.get(order.getProperty())))
                        .collect(Collectors.toList());
                query.orderBy(orders);
            }

            // Fetch and map
            List<?> entities = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            Class<?> finalResponseType = responseType;
            List<?> dtos = entities.stream()
                    .map(entity -> mapToDto(entity, searchDto.getSearchEntity()))
                    .collect(Collectors.toList());

            // Count query
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(entityClass);
            countQuery.select(cb.count(countRoot));

            // Rebuild predicates for the countRoot
            List<Predicate> countPredicates = buildPredicates(searchDto, cb, countRoot);

            if (!countPredicates.isEmpty()) {
                countQuery.where(cb.or(countPredicates.toArray(new Predicate[0])));
            }

            Long total = entityManager.createQuery(countQuery).getSingleResult();
            Page<?> page = new PageImpl<>(dtos, pageable, total);

            return new SearchResponseDto(page, searchDto.getSearchFields(), searchDto.getQuery());

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity not found: " + searchDto.getSearchEntity());
        }
    }
    private Object mapToDto(Object entity, String entityName) {
        EntityMapper<Object, Object> mapper = mapperRegistry.getMapper(entityName);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper registered for entity: " + entityName);
        }
        return mapper.toDto(entity);
    }
    private List<Predicate> buildPredicates(SearchDto searchDto, CriteriaBuilder cb, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (searchDto.getQuery() == null || searchDto.getQuery().isEmpty()) {
            return predicates;
        }
        if (searchDto.getSearchFields() == null || searchDto.getSearchFields().isEmpty()
                || searchDto.getSearchFields().get(0).isEmpty()) {
            return predicates;
        }
        String searchTerm = searchDto.getQuery().toLowerCase(Locale.ROOT);

        for (String field : searchDto.getSearchFields()) {
            try {
                Path<?> path = resolvePath(root, field);

                Class<?> javaType = path.getJavaType();

                if (javaType.equals(String.class)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + searchTerm + "%"));
                } else if (javaType.equals(java.util.Date.class) || javaType.equals(java.sql.Timestamp.class)) {
                    LocalDateTime parsedDate = parseLocalDateTime(searchTerm);
                    if (parsedDate != null) {
                        predicates.add(cb.equal(path.as(LocalDateTime.class), parsedDate));
                    }
                } else {
                    predicates.add(cb.like(path.as(String.class), "%" + searchTerm + "%"));
                }
            } catch (IllegalArgumentException e) {

                log.error("Field not found: {}", field);
                continue;
            }
        }

        return predicates;
    }

    private LocalDateTime parseLocalDateTime(String input) {
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

        for (String part : parts) {
            path = path.get(part);
        }

        return (Path<String>) path;
    }
}
