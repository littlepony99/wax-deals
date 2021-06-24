package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public interface UserService {

    boolean add(UserChangeProfileInfoRequest userProfileInfo);

    boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    boolean delete(User user, ModelAndView modelAndView);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> signInCheck(String email, String password);

    Optional<User> signInCheck(UserChangeProfileInfoRequest userProfileInfo);

    Optional<User> editProfile(UserChangeProfileInfoRequest userProfileInfo,
                               User user,
                               ModelAndView modelAndView);

}
