package com.rabbtor.example.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration

@SpringBootApplication
@EnableAutoConfiguration(exclude = ThymeleafAutoConfiguration)
class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
