package com.vinylteam.vinyl;

import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class ApplicationCommandLineRunner implements CommandLineRunner {

    //@Autowired
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        UserInfoRequest request = UserInfoRequest.builder()
                .email("another_user@gmail.com")
                .password("TestPassword24")
                .passwordConfirmation("TestPassword24")
                .build();
        //var user = securityService.createUserWithHashedPassword("test_user@gmail.com","superPAssword".toCharArray());
        userService.register(request);
        //updater.updateUniqueVinylsRewriteOffers();
    }
}

