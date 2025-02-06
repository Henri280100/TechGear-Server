package com.v01.techgear_server.serviceimpls;

import java.util.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.v01.techgear_server.model.Product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.enums.SortDirection;

public class ProductFilterSpecification {

    private ProductFilterSpecification() {
        // Private constructor to prevent instantiation.
    }

    public static Specification<Product> getProductSpecification(ProductFilterDTO filterDTO) {
        return Specification
                .where(createComplexSpecification(filterDTO))
                .and(applySorting(filterDTO));
    }

    public static Specification<Product> createComplexSpecification(ProductFilterDTO filterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Name Filter (Case-insensitive, Partial Match)
            Optional.ofNullable(filterDTO.getName())
                    .filter(StringUtils::hasText)
                    .ifPresent(name -> predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("name")),
                                    "%" + name.toLowerCase() + "%")));

            // Price Range Filters
            Optional.ofNullable(filterDTO.getMinPrice())
                    .ifPresent(minPrice -> predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)));

            Optional.ofNullable(filterDTO.getMaxPrice())
                    .ifPresent(maxPrice -> predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)));

            // Exact Match Filters
            addEqualPredicate(predicates, root, criteriaBuilder, "category", filterDTO.getCategory());
            addEqualPredicate(predicates, root, criteriaBuilder, "brand", filterDTO.getBrand());

            // Multiple Categories Filter
            Optional.ofNullable(filterDTO.getCategories())
                    .filter(categories -> !categories.isEmpty())
                    .ifPresent(categories -> predicates.add(root.get("category").in(categories)));

            // Combine all predicates
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Multiple Categories Specification
     */
    public static Specification<Product> filterByMultipleCategories(List<String> categories) {
        return (root, query, criteriaBuilder) -> Optional.ofNullable(categories)
                .filter(list -> !list.isEmpty())
                .map(list -> root.get("category").in(list))
                .orElse(null);
    }

    /**
     * Helper method for adding equal predicates
     */
    private static void addEqualPredicate(
            List<Predicate> predicates,
            Root<Product> root,
            CriteriaBuilder criteriaBuilder,
            String fieldName,
            Object value) {
        Optional.ofNullable(value)
                .ifPresent(v -> predicates.add(
                        criteriaBuilder.equal(root.get(fieldName), v)));
    }

    /**
     * Advanced Sorting Specification
     */
    public static Specification<Product> applySorting(ProductFilterDTO filterDTO) {
        return (root, query, criteriaBuilder) -> {
            // If sorting is required, modify the query
            if (StringUtils.hasText(filterDTO.getSortField())) {
                query.orderBy(
                        filterDTO.getSortDirection() == SortDirection.DESC
                                ? criteriaBuilder.desc(root.get(filterDTO.getSortField()))
                                : criteriaBuilder.asc(root.get(filterDTO.getSortField())));
            }
            return null;
        };
    }
}