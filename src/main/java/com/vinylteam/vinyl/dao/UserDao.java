package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserDao {

    boolean add(User user);

    boolean delete(User user);

    boolean update(String email, User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

}
