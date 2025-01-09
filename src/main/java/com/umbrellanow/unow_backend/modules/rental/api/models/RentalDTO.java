package com.umbrellanow.unow_backend.modules.rental.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RentalDTO {
    private String orderID;
    private String rentalType;
    private long umbrellaID;
}
