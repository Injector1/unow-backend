package com.umbrellanow.unow_backend.modules.rental.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RentalDTO {
    private String rentalStatus;
    private String rentalType;
    private LocalDateTime startDate;
    private long umbrellaID;
    private double priceRate;
}
