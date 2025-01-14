package com.umbrellanow.unow_backend.shared.enumeration;

public enum RentalType {
    DAILY("daily"),
    HOURLY("hourly");

    private final String typeName;

    RentalType(String typeName) {
        this.typeName = typeName;
    }

    public static RentalType parseType(String type) {
        for (RentalType value : values()) {
            if (value.typeName.equals(type.strip().toLowerCase())) {
                return value;
            }
        }
        return null;
    }
}
