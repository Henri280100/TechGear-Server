package com.v01.techgear_server.enums;

import lombok.Getter;

@Getter
public enum PhoneNumberPurpose {
    ORDER_UPDATES("Order Updates"),
    DELIVERY_CONTACT("Delivery Contact"),
    CUSTOMER_SUPPORT("Customer Support"),
    BILLING_CONTACT("Billing Contact"),
    MARKETING("Marketing"),
    OTHER("Other");

    private final String displayName;

    PhoneNumberPurpose(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}