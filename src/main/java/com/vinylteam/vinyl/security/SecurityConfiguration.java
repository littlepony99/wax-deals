package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.security.impl.SpringSecurityService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.web.filter.JwtValidatorFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import java.util.List;

import static com.vinylteam.vinyl.security.SecurityConstants.JWT_VALIDATOR_FILTER_BEAN_NAME;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServletContext context;

    private DefaultLogoutService logoutHandlerService;

    @Value("#{${cors.allowedOrigins}}")
    private List<String> allowedOrigins;

    @Value("#{${cors.allowedMethods}}")
    private List<String> allowedMethods;

    @Autowired
    void init(DefaultLogoutService logoutService) {
        this.logoutHandlerService = logoutService;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**",
                "/img/**",
                "/favicon.ico",
                "/fonts/**",
                "/*.js");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(getJwtValidatorFilterDelegatingProxy(), UsernamePasswordAuthenticationFilter.class)
                .formLogin().loginPage("/signIn").permitAll().usernameParameter("email").defaultSuccessUrl("/profile")
                .and()
                .logout().addLogoutHandler(logoutHandlerService).logoutSuccessUrl("/successlogout");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.get(0), allowedOrigins.get(1), allowedOrigins.get(2), allowedOrigins.get(3))
                        .allowedMethods(allowedMethods.get(0), allowedMethods.get(1), allowedMethods.get(2), allowedMethods.get(3));
            }
        };
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(SpringSecurityService securityService) {
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

    @Bean
    public SecurityService getSecurityService(PasswordEncoder encoder) {
        DefaultSecurityService securityService = new DefaultSecurityService();
        securityService.setEncoder(encoder);
        return securityService;
    }

    @Bean
    public JwtValidatorFilter jwtValidatorFilter(UserService userService, JwtService jwtService) {
        JwtValidatorFilter jwtValidatorFilter = new JwtValidatorFilter(userService);
        jwtValidatorFilter.setJwtService(jwtService);
        return jwtValidatorFilter;
    }

    @Bean
    public JwtService jwtTokenProvider(UserService userService,
                                       UserMapper userMapper,
                                       UserDetailsService userDetailsService,
                                       AuthenticationManager authManager) {
        var jwtService = new JwtTokenProvider(userService, userMapper, userDetailsService);
        jwtService.setAuthenticationManager(authManager);
        return jwtService;
    }

    @Bean
    public DefaultLogoutService logoutService(JwtService jwtService,
                                              InMemoryLogoutTokenService logoutStorageService) {
        return new DefaultLogoutService(jwtService, logoutStorageService);
    }

    protected DelegatingFilterProxy getJwtValidatorFilterDelegatingProxy() {
        DelegatingFilterProxy delegateFilterProxy = new DelegatingFilterProxy();
        delegateFilterProxy.setServletContext(context);
        delegateFilterProxy.setTargetBeanName(JWT_VALIDATOR_FILTER_BEAN_NAME);
        return delegateFilterProxy;
    }

}
