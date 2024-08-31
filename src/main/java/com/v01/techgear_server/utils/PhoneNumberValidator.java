package com.v01.techgear_server.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberValidator {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, countryCode);
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public String formatPhoneNumber(String phoneNumber, String countryCode) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, countryCode);
            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            return null;
        }
    }
}
