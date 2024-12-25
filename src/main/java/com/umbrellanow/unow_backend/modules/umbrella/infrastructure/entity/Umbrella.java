package com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity;

import com.umbrellanow.unow_backend.modules.storage.infrastructure.entity.StorageBox;
import com.umbrellanow.unow_backend.shared.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Umbrella extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private UmbrellaGroup umbrellaGroup;
    @OneToOne
    @JoinColumn(name = "box_id")
    private StorageBox storageBox;
    private boolean isCurrentlyLeased;
    private LocalDateTime lastLeaseDate;
    private String s3Path;
}
