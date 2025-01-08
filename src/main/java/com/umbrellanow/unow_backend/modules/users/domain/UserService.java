package com.umbrellanow.unow_backend.modules.users.domain;

import com.umbrellanow.unow_backend.modules.users.infrastructure.entity.User;

public interface UserService {
    User getUserByID(long userID);
    User getUserByEmail(String email);
}
