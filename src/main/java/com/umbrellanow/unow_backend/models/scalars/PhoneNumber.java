package com.umbrellanow.unow_backend.models.scalars;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import java.util.regex.Pattern;

@Value
public class PhoneNumber {
    String number;

    private static final String PHONE_REGEX = "^\\+?[0-9. ()-]{7,25}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public PhoneNumber(String number) {
        if (number == null || !PHONE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.number = number;
    }

    @JsonValue
    public String getNumber() {
        return number;
    }
}
