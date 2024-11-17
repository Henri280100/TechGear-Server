package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.enums.UserPermission;
import com.v01.techgear_server.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Criteria for searching and filtering users")
public class UserSearchCriteria {
    // Constants
    private static final class SearchConstants {
        static final int MAX_PAGE_SIZE = 100;
        static final int DEFAULT_PAGE_SIZE = 10;
        static final String DEFAULT_SORT_FIELD = "userId";
        static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
                "userId", "username", "email", "firstName", "lastName",
                "createdAt", "status", "lastLoginAt", "active");
    }

    // Search and Filter Fields
    @Schema(description = "Search query string")
    private String searchQuery;

    @Schema(description = "User roles to filter")
    private Set<Roles> roles;

    @Schema(description = "User status filter")
    private UserStatus status;

    @Schema(description = "Minimum creation date filter")
    private LocalDateTime minCreatedAt;

    @Schema(description = "Maximum creation date filter")
    private LocalDateTime maxCreatedAt;

    @Schema(description="User permission to filer")
    private Set<UserPermission> permissions;

    // Pagination
    @Builder.Default
    @Min(value = 0, message = "Page number must be non-negative")
    @Schema(description = "Page number", defaultValue = "0")
    private int page = 0;

    @Builder.Default
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Schema(description = "Number of items per page", defaultValue = "10")
    private int size = SearchConstants.DEFAULT_PAGE_SIZE;

    // Sorting
    @Builder.Default
    private List<String> sortBy = new ArrayList<>(Collections.singletonList(SearchConstants.DEFAULT_SORT_FIELD));

    @Builder.Default
    private Sort.Direction sortDirection = Sort.Direction.ASC;

    // Validation Methods
    public void validate() {
        validatePagination();
        validateSortFields();
        validateDateRange();
    }

    private void validatePagination() {
        this.page = Math.max(0, this.page);
        this.size = Math.min(Math.max(1, this.size), SearchConstants.MAX_PAGE_SIZE);
    }

    private void validateSortFields() {
        this.sortBy = Optional.ofNullable(this.sortBy)
                .map(this::filterValidSortFields)
                .filter(fields -> !fields.isEmpty())
                .orElse(new ArrayList<>(Collections.singletonList(SearchConstants.DEFAULT_SORT_FIELD)));
    }

    private List<String> filterValidSortFields(List<String> fields) {
        return fields.stream()
                .filter(SearchConstants.ALLOWED_SORT_FIELDS::contains)
                .collect(Collectors.toList());
    }

    private void validateDateRange() {
        if (minCreatedAt != null && maxCreatedAt != null && minCreatedAt.isAfter(maxCreatedAt)) {
            throw new IllegalArgumentException("Minimum creation date must be before maximum creation date");
        }
    }

    // Pageable Creation
    public Pageable toPageable() {
        validate();
        Sort sort = createSort();
        return PageRequest.of(page, size, sort);
    }

    private Sort createSort() {
        return sortBy.isEmpty()
                ? Sort.by(sortDirection, SearchConstants.DEFAULT_SORT_FIELD)
                : Sort.by(sortDirection, sortBy.toArray(new String[0]));
    }

    public boolean hasPermissionCriteria() {
        return permissions != null && !permissions.isEmpty();
    }

    // Static builder method
    public static UserSearchCriteriaBuilder builder() {
        return new UserSearchCriteriaBuilder();
    }

    // Custom Builder Class
    public static class UserSearchCriteriaBuilder {
        private UserSearchCriteria criteria = new UserSearchCriteria();

        public UserSearchCriteriaBuilder searchQuery(String searchQuery) {
            criteria.setSearchQuery(searchQuery);
            return this;
        }

        public UserSearchCriteriaBuilder sortBy(String sortField) {
            List<String> validSortFields = Optional.ofNullable(sortField)
                    .map(field -> Collections.singletonList(field))
                    .map(fields -> fields.stream()
                            .filter(SearchConstants.ALLOWED_SORT_FIELDS::contains)
                            .collect(Collectors.toList()))
                    .filter(fields -> !fields.isEmpty())
                    .orElse(new ArrayList<>(Collections.singletonList(SearchConstants.DEFAULT_SORT_FIELD)));

            criteria.setSortBy(validSortFields);
            return this;
        }

        public UserSearchCriteriaBuilder sortBy(List<String> sortFields) {
            List<String> validSortFields = Optional.ofNullable(sortFields)
                    .map(fields -> fields.stream()
                            .filter(SearchConstants.ALLOWED_SORT_FIELDS::contains)
                            .collect(Collectors.toList()))
                    .filter(fields -> !fields.isEmpty())
                    .orElse(new ArrayList<>(Collections.singletonList(SearchConstants.DEFAULT_SORT_FIELD)));

            criteria.setSortBy(validSortFields);
            return this;
        }

        // Other builder methods...

        public UserSearchCriteria build() {
            return criteria;
        }
    }

    // Helper Methods
    public boolean hasSearchCriteria() {
        return Optional.ofNullable(searchQuery).isPresent() ||
                Optional.ofNullable(roles).isPresent() ||
                Optional.ofNullable(status).isPresent() ||
                Optional.ofNullable(minCreatedAt).isPresent() ||
                Optional.ofNullable(maxCreatedAt).isPresent();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserSearchCriteria.class.getSimpleName() + "[", "]")
                .add("page=" + page)
                .add("size=" + size)
                .add("sortBy=" + sortBy)
                .add("sortDirection=" + sortDirection)
                .add("hasSearchQuery=" + (searchQuery != null))
                .add("rolesCount=" + (roles != null ? roles.size() : 0))
                .add("status=" + status)
                .add("dateRange=[" + minCreatedAt + " to " + maxCreatedAt + "]")
                .toString();
    }
}