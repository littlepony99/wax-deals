package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public interface UserService {

    boolean add(String email, String password, String discogsUserName);

    boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName);

    boolean delete(User user, ModelAndView modelAndView, HttpSession session);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> signInCheck(String email, String password);

    Optional<User> signInCheck(String email, String password, String token);

    void editProfile(String newEmail,
                     String oldPassword,
                     String newPassword,
                     String confirmNewPassword,
                     String newDiscogsUserName,
                     User user,
                     ModelAndView modelAndView,
                     HttpSession session);

}
