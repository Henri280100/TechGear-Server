package com.v01.techgear_server.utils;

import com.v01.techgear_server.enums.ProductStatus;

public class CalculateHype {
    public static String calculateHype(ProductStatus status, long daysSinceRelease) {
        if (status == ProductStatus.COMING_SOON) {
            long daysUntilRelease = Math.abs(daysSinceRelease);
            if (daysUntilRelease <= 3) return "5";
            else if (daysUntilRelease <= 7) return "4";
            else if (daysUntilRelease <= 14) return "3";
            else if (daysUntilRelease <= 30) return "2";
            else return "1";
        } else {
            if (daysSinceRelease == 0) return "5";
            else if (daysSinceRelease <= 3) return "4";
            else if (daysSinceRelease <= 7) return "3";
            else if (daysSinceRelease <= 14) return "2";
            else return "1";
        }
    }
}
