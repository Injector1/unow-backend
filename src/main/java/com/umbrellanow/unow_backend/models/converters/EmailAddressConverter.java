package com.umbrellanow.unow_backend.models.converters;

import com.umbrellanow.unow_backend.models.scalars.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailAddressConverter implements AttributeConverter<EmailAddress, String> {
    @Override
    public String convertToDatabaseColumn(EmailAddress emailAddress) {
        return (emailAddress == null) ? null : emailAddress.getEmail();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isEmpty()) ? null : new EmailAddress(dbData);
    }
}
