package com.umbrellanow.unow_backend.modules.users.infrastructure;

import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;
import com.umbrellanow.unow_backend.shared.scalars.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(EmailAddress email);
}
