package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
/*@Controller
@RequestMapping("/emailConfirmation")*/
@CrossOrigin(origins = {"http://localhost:3000", "http://react-wax-deals.herokuapp.com"})
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @GetMapping
    public ModelAndView getConfirmationPage(@RequestParam(value = "token") String tokenAsString) {
        ModelAndView modelAndView = new ModelAndView();
        emailConfirmationService.findByToken(tokenAsString);

        modelAndView.setStatus(HttpStatus.OK);
        log.debug("Set response status to {'status':{}}", HttpStatus.OK);
        modelAndView.addObject("token", tokenAsString);
        modelAndView.setViewName("confirmation-signin");
        return modelAndView;
    }




    private Map<String, String> getStatusInfoMap(String code, String s) {
        return Map.of(
                "resultCode", code,
                "message", s);
    }

    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        String username = authUser.getUsername();
        User byEmail = userService.findByEmail(username);
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "token", token);
    }

}
