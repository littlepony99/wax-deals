package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.filter.JwtValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    @Bean
    public JwtValidatorFilter getJwtValidatorFilter(JwtService jwtService, UserService userService) {
        return new JwtValidatorFilter(jwtService, userService);
    }

}
