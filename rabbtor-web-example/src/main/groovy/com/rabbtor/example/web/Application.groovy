package com.rabbtor.example.web

import com.rabbtor.web.config.EnableRabbtorWebMvc
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableRabbtorWebMvc
class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
