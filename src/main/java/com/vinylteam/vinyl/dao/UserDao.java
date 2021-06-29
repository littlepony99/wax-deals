package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface UserDao {

    long add(User user);

    void delete(User user);

    void update(String email, User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    void setUserStatusTrue(long userId);

}
