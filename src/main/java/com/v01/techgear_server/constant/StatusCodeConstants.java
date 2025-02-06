package com.v01.techgear_server.constant;

public class StatusCodeConstants {
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 0;

    // Prevent instantiation
    private StatusCodeConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
