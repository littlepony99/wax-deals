package com.vinylteam.vinyl.web.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SecurityFilter> registrationBean() {
        FilterRegistrationBean<SecurityFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityFilter());
        registrationBean.addUrlPatterns("/signOut");
        registrationBean.addUrlPatterns("/profile");
        registrationBean.addUrlPatterns("/editProfile");
        registrationBean.addUrlPatterns("/deleteProfile");
        return registrationBean;
    }
}
