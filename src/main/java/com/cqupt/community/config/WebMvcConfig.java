package com.cqupt.community.config;

import com.cqupt.community.controller.interceptor.DataInterceptor;
import com.cqupt.community.controller.interceptor.LoginRequiredInterceptor;
import com.cqupt.community.controller.interceptor.LoginTicketInterceptor;
import com.cqupt.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    MessageInterceptor messageInterceptor;

    @Autowired
    DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
//        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.js", "/**/*.css",
//                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(messageInterceptor).excludePathPatterns("/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(dataInterceptor).excludePathPatterns("/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
