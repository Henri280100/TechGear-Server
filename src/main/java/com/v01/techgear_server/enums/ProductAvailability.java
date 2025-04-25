package com.v01.techgear_server.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum ProductAvailability {
    IN_STOCK("Available"),
    OUT_OF_STOCK("Out of Stock"),
    CONTACT("Contact Us"),
    PRE_ORDER("Pre-order"),
    BACK_ORDER("Back Order"),
    DISCONTINUED("Discontinued"),
    UNAVAILABLE("Unavailable");

    private final String displayName;

    private static final Logger logger = LoggerFactory.getLogger(ProductAvailability.class);

    ProductAvailability(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ProductAvailability fromValue(String value) {
        for (ProductAvailability availability : ProductAvailability.values()) {
            if (availability.displayName.equalsIgnoreCase(value) || availability.name().equalsIgnoreCase(value)) {
                return availability;
            }
        }
        // Log the invalid input
        logger.warn("Unknown ProductAvailability: {}", value);
        // Return a default value (could be IN_STOCK, UNAVAILABLE, or any other fallback)
        return ProductAvailability.UNAVAILABLE;
    }

}
