package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public interface UserService {

    void register(UserInfoRequest userProfileInfo);

    void update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    void delete(User user);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    void signInCheck(UserInfoRequest userProfileInfo);

    Optional<User> signInCheckConfirmation(UserInfoRequest userProfileInfo);

    Optional<User> editProfile(UserInfoRequest userProfileInfo,
                               User user);

}
