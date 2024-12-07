package com.umbrellanow.unow_backend.modules.users.infrastructure.entity;

import com.umbrellanow.unow_backend.shared.converters.EmailAddressConverter;
import com.umbrellanow.unow_backend.shared.converters.PhoneNumberConverter;
import com.umbrellanow.unow_backend.shared.entity.AbstractEntity;
import com.umbrellanow.unow_backend.shared.enumeration.UserGroup;
import com.umbrellanow.unow_backend.shared.enumeration.UserStatus;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import com.umbrellanow.unow_backend.shared.scalars.PhoneNumber;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "un_user")
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Convert(converter = EmailAddressConverter.class)
    private EmailAddress email;
    @Convert(converter = PhoneNumberConverter.class)
    private PhoneNumber phoneNumber;
    @Enumerated(EnumType.STRING)
    private UserGroup userGroup;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}