package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;

import java.util.Optional;

public interface UserService {

    void register(UserInfoRequest userProfileInfo);

    void confirmEmailByToken(String token);

    void update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    void delete(User user);

    Optional<User> findById(long id);

    User findByEmail(String email);

    User changeDiscogsUserName(String discogsUserNAme, User user);

    User changeUserPassword(UserInfoRequest password, User user);

    void signInCheck(UserInfoRequest userProfileInfo);

    User editProfile(UserInfoRequest userProfileInfo,
                     User user);

}
