package com.umbrellanow.unow_backend.modules.auth.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAndCodeDTO {
    private String email;
    private String code;
}
