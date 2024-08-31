package com.v01.techgear_server.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidation {
    /**
     * 
     * ^ represents starting character of the string.
     * (?=.*[0-9]) represents a digit must occur at least once.
     * (?=.*[a-z]) represents a lower case alphabet must occur at least once.
     * (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
     * (?=.*[@#$%^&-+=()] represents a special character that must occur at least
     * once.
     * (?=\\S+$) white spaces don’t allowed in the entire string.
     * .{8, 20} represents at least 8 characters and at most 20 characters.
     * $ represents the end of the string.
     * 
     */
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
    private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValidPassword(String newPassword) {

        if (newPassword == null) {
            return false;
        }

        final Matcher matcher = PATTERN.matcher(newPassword);

        return matcher.matches();
    }
}
