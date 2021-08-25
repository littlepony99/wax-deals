package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String getProfilePage(@SessionAttribute(value = "user", required = false) User user,
                                 Model model) {
        WebUtils.setUserAttributes(user, model);
        return "profile";
    }

    @GetMapping(path = "/edit-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String getEditProfilePage(@SessionAttribute(value = "user", required = false) User user,
                                     Model model) {
        WebUtils.setUserAttributes(user, model);
        return "editProfile";
    }

    @PostMapping(path = "/edit-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ModelAndView editProfile(@RequestParam(value = "email") String newEmail,
                                    @RequestParam(value = "oldPassword") String oldPassword,
                                    @RequestParam(value = "newPassword") String newPassword,
                                    @RequestParam(value = "confirmNewPassword") String confirmNewPassword,
                                    @RequestParam(value = "discogsUserName") String newDiscogsUserName,
                                    HttpSession session,
                                    @SessionAttribute("user") User user) throws ServerException {

        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .email(newEmail)
                .password(oldPassword)
                .newPassword(newPassword)
                .newPasswordConfirmation(confirmNewPassword)
                .discogsUserName(newDiscogsUserName)
                .build();
        if (user != null) {
            ModelAndView modelAndView = new ModelAndView("editProfile");
            User userAfterEdit = userService.editProfile(userProfileInfo, user);
            modelAndView.addObject("userRole", user.getRole().getName());
            session.setAttribute("user", userAfterEdit);
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/signIn");
        }
    }

    @PostMapping(path = "/delete-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ModelAndView deleteProfile(HttpSession session, @SessionAttribute("user") User user) {
        if (user != null) {
            ModelAndView modelAndView = new ModelAndView("redirect:/signUp");
            userService.delete(user);
            session.invalidate();
            return modelAndView;
        } else {
            return new ModelAndView("redirect:/signIn");
        }
    }

}
