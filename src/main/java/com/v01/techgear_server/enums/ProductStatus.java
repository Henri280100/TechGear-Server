package com.v01.techgear_server.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ProductStatus {
    NEW("New Arrival"),
    HOT("Hot"),
    TRENDING("Trending Now"),
    DISCOUNTED("Discounted"),
    COMING_SOON("Coming Soon"),
    LIMITED_EDITION("Limited Edition"),
    BEST_SELLER("Best Seller"),
    NORMAL("Normal"),
    ON_SALE("On Sale");

    private final String label;
    ProductStatus(String label) {
        this.label = label;
    }

    @JsonCreator
    public static ProductStatus fromValue(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.label.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ProductStatus: " + value);
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

}
