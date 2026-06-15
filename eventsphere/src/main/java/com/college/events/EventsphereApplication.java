package com.college.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EventsphereApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventsphereApplication.class, args);
    }
}