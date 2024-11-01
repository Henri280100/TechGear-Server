package com.v01.techgear_server.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneNumberValidator {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();


    public boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, countryCode);
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            log.error("Error parsing phone number: {}", e.getMessage(), e);
            return false;
        }
    }

    public String formatPhoneNumber(String phoneNumber, String countryCode) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, countryCode);
            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            log.error("Error formatting phone number", e);
            return null;
        }
    }
}
