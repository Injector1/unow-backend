package com.umbrellanow.unow_backend.models;

import com.umbrellanow.unow_backend.models.enumeration.DiscountType;
import com.umbrellanow.unow_backend.models.enumeration.UserGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Discount extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private UserGroup userGroup;
    @Enumerated(EnumType.STRING)
    private DiscountType type;
    private Double value;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;


    @ManyToOne
    @JoinColumn(name = "umbrella_group_id")
    private UmbrellaGroup umbrellaGroup;
}
