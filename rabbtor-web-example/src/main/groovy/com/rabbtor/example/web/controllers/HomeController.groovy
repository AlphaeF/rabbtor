package com.rabbtor.example.web.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(path = '/')
class HomeController
{

    @RequestMapping(path = '/')
    def index() {
        new ModelAndView('/home/index',[id:20])
    }
}
