package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public interface UserService {

    void add(UserChangeProfileInfoRequest userProfileInfo);

    void update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    void delete(User user, ModelAndView modelAndView);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> signInCheck(String email, String password);

    Optional<User> signInCheck(UserChangeProfileInfoRequest userProfileInfo);

    Optional<User> editProfile(UserChangeProfileInfoRequest userProfileInfo,
                               User user,
                               ModelAndView modelAndView);

}
