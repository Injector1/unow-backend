package com.umbrellanow.unow_backend.modules.umbrella.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UmbrellaPriceRateResponse {
    private double hourlyRate;
    private double dailyRate;
    private double deposit;
}
