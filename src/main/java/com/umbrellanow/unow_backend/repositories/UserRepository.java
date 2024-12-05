package com.umbrellanow.unow_backend.repositories;

import com.umbrellanow.unow_backend.models.User;
import com.umbrellanow.unow_backend.models.scalars.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(EmailAddress email);
}
