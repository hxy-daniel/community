package com.cqupt.community.config;

import com.cqupt.community.controller.intercepter.LoginRequiredIntercepter;
import com.cqupt.community.controller.intercepter.LoginTicketIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginTicketIntercepter loginTicketIntercepter;

    @Autowired
    LoginRequiredIntercepter loginRequiredIntercepter;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketIntercepter).excludePathPatterns("/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(loginRequiredIntercepter).excludePathPatterns("/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
