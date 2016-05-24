package com.rabbtor.example.web

import com.rabbtor.gsp.config.annotation.EnableWebGsp
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration

@SpringBootApplication
@EnableAutoConfiguration(exclude = ThymeleafAutoConfiguration)
@EnableWebGsp()
class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
