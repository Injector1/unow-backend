package com.umbrellanow.unow_backend.modules.rental.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CaptureFinalPaymentDTO {
    private String orderID;
    private long umbrellaID;
}
