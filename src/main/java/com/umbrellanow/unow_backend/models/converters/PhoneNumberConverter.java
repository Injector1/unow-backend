package com.umbrellanow.unow_backend.models.converters;

import com.umbrellanow.unow_backend.models.scalars.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {
    @Override
    public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
        return (phoneNumber == null) ? null : phoneNumber.getNumber();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isEmpty()) ? null : new PhoneNumber(dbData);
    }
}
