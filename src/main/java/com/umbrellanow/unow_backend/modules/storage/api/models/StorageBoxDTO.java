package com.umbrellanow.unow_backend.modules.storage.api.models;

import com.umbrellanow.unow_backend.shared.dto.AbstractDTOWithID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageBoxDTO extends AbstractDTOWithID {
    private int boxNumber;
    private int boxCode;
}
