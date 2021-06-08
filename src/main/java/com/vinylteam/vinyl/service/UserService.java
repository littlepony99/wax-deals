package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    boolean add(String email, String password, String discogsUserName);

    boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    boolean delete(User user);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> signInCheck(String email, String password);

    Optional<User> signInCheck(String email, String password, UUID token);
}
