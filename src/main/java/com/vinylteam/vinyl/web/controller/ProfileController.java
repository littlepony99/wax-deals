package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/profile", produces = "text/html;charset=UTF-8")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String getProfilePage(@SessionAttribute(value = "user", required = false) User user,
                                 Model model) {
        WebUtils.setUserAttributes(user, model);
        return "profile";
    }

    @GetMapping(path = "/edit-profile")
    public String getEditProfilePage(@SessionAttribute(value = "user", required = false) User user,
                                     Model model) {
        WebUtils.setUserAttributes(user, model);
        return "editProfile";
    }

    @PostMapping(path = "/edit-profile")
    public ModelAndView editProfile(@RequestParam(value = "email") String newEmail,
                                    @RequestParam(value = "oldPassword") String oldPassword,
                                    @RequestParam(value = "newPassword") String newPassword,
                                    @RequestParam(value = "confirmNewPassword") String confirmNewPassword,
                                    @RequestParam(value = "discogsUserName") String newDiscogsUserName,
                                    HttpSession session,
                                    @SessionAttribute("user") User user) {
        UserChangeProfileInfoRequest userProfileInfo = UserChangeProfileInfoRequest.builder()
                .email(newEmail)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .confirmNewPassword(confirmNewPassword)
                .newDiscogsUserName(newDiscogsUserName)
                .build();
        if (user != null) {
            ModelAndView modelAndView = new ModelAndView("editProfile");
            User userAfterEdit = userService.editProfile(userProfileInfo, user, modelAndView).orElse(user);
            session.setAttribute("user", userAfterEdit);
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/signIn");
        }
    }

    @PostMapping(path = "/delete-profile")
    public ModelAndView deleteProfile(HttpSession session, @SessionAttribute("user") User user) {
        if (user != null) {
            ModelAndView modelAndView = new ModelAndView("redirect:/signUp");
            if (userService.delete(user, modelAndView)) {
                session.invalidate();
            }
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/signIn");
        }
    }

}
