package com.umbrellanow.unow_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class PriceRate extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Double dailyRate;
    private Double hourlyRate;

    @OneToMany(mappedBy = "priceRate")
    private Set<UmbrellaGroup> umbrellaGroups;
}
