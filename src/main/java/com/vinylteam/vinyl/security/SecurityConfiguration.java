package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.security.impl.SpringSecurityService;
import com.vinylteam.vinyl.web.filter.JwtValidatorFilter;
import com.vinylteam.vinyl.web.filter.UserAttributeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final String algorithm = "PBKDF2WithHmacSHA512";

    private final SpringSecurityService securityService;

    private final UserAttributeFilter userAttributeFilter;
    private final JwtValidatorFilter jwtValidatorField;
    private final SecurityService defaultSecurityService;

    @PostConstruct
    void init(){
        defaultSecurityService.setEncoder(encoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                //static resources
                "/css/**", "/img/**", "/favicon.ico", "/fonts/**", "/*.js");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                //.sessionManagement()
                //.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .addFilterAfter(userAttributeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtValidatorField, UsernamePasswordAuthenticationFilter.class)
                .formLogin().loginPage("/signIn").permitAll().usernameParameter("email").defaultSuccessUrl("/profile");/*.successHandler()*///;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(securityService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("keySecret", 20, 10000, 256);
        pbkdf2PasswordEncoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
        pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
        return pbkdf2PasswordEncoder;
    }

}
