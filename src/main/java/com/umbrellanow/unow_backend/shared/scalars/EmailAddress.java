package com.umbrellanow.unow_backend.shared.scalars;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import java.util.regex.Pattern;

@Value
public class EmailAddress {
    String email;

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    public EmailAddress(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.email = email;
    }

    @JsonValue
    public String getEmail() {
        return email;
    }
}
