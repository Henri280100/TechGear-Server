package com.v01.techgear_server.constant;

public class SecurityConstants {

    public static final String JWT_SECRET = "your_jwt_secret_key";
    public static final long JWT_EXPIRATION_MS = 86400000; // 1 day

    // Prevent instantiation
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    } 
}