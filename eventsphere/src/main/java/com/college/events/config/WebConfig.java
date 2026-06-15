package com.college.events.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Force Spring Boot to cleanly scan and serve everything in the static directory
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}