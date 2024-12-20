package com.umbrellanow.unow_backend.modules.umbrella.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmbrellaPriceRateResponse {
    private double hourlyRate;
    private double dailyRate;
    private double deposit;
}
