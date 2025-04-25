package com.v01.techgear_server.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DiscountType {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("Fixed Amount"),
    BOGO("Buy One Get One"),
    FREE_SHIPPING("Free Shipping"),
    SEASONAL("Seasonal"),
    VOLUME("Volume"),
    LOYALTY("Loyalty"),
    STUDENT("Student"),
    REFERRAL("Referral"),
    FLASH_SALE("Flash Sale"),
    PROMO_CODE("Promo Code"),
    MEMBERSHIP("Membership"),
    CLEARANCE("Clearance"),
    BUNDLED("Bundled"),
    FIRST_TIME_BUYER("First Time Buyer"),
    CASHBACK("Cashback"),
    TRADE_IN("Trade-In"),
    TIME_LIMITED("Time Limited"),
    AMOUNT("Amount");

    private final String label;

    DiscountType(String label) {
        this.label = label;
    }

    @JsonCreator
    public static DiscountType fromValue(String value) {
        for (DiscountType type : DiscountType.values()) {
            if (type.label.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DiscountType: " + value);
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}