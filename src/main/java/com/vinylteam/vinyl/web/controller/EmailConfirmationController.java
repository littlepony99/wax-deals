package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/emailConfirmation")
public class EmailConfirmationController {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;

    @GetMapping
    public ModelAndView getConfirmationPage(@RequestParam(value = "token") String tokenAsString) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<ConfirmationToken> optionalConfirmationToken = emailConfirmationService.findByToken(tokenAsString);

        modelAndView.setStatus(HttpStatus.OK);
        log.debug("Set response status to {'status':{}}", HttpStatus.OK);
        modelAndView.addObject("token", tokenAsString);
        modelAndView.setViewName("confirmation-signin");
        return modelAndView;
    }

}
