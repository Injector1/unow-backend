package com.umbrellanow.unow_backend.modules.rental.infrastructure.entity;

import com.umbrellanow.unow_backend.modules.discount.infrastructure.entity.Discount;
import com.umbrellanow.unow_backend.modules.transaction.infrastructure.entity.Transaction;
import com.umbrellanow.unow_backend.modules.umbrella.infrastructure.entity.Umbrella;
import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.entity.AbstractEntity;
import com.umbrellanow.unow_backend.shared.enumeration.RentalStatus;
import com.umbrellanow.unow_backend.shared.enumeration.RentalType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Rental extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RentalStatus status;
    @Enumerated(EnumType.STRING)
    private RentalType type;
    private LocalDateTime returnedAt;
    private Double totalCost;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "umbrella_id")
    private Umbrella umbrella;
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;
    @OneToMany(mappedBy = "associatedRental", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
