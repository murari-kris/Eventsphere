package com.college.events.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Direct root and clean paths straight to your raw physical HTML resources
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure Spring Boot actively indexes and exposes files in the static folder directly
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}