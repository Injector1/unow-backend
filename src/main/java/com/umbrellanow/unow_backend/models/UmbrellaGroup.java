package com.umbrellanow.unow_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class UmbrellaGroup extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String description;
    private Double defaultDeposit;

    @OneToMany(mappedBy = "umbrellaGroup")
    private List<Umbrella> associatedUmbrellas;

    @ManyToOne
    @JoinColumn(name = "price_rate_id")
    private PriceRate priceRate;
}
