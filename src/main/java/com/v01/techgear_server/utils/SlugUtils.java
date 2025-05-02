package com.v01.techgear_server.utils;

public class SlugUtils {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Remove special chars
        return input
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")  // Remove special chars
                .replaceAll("\\s+", "-");        // Replace spaces with -
    }
}
