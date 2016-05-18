package com.rabbtor.example.web.controllers

import com.rabbtor.example.web.models.EditPersonModel
import com.rabbtor.web.servlet.mvc.util.MvcUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(path = '/')
class HomeController
{

    @RequestMapping(path = '/')
    def index() {
        new ModelAndView('/home/index',
                [id:20, cmd: new EditPersonModel()  ])
    }

    @RequestMapping(path = '/list/{id}')
    def list(@RequestParam() int id, @RequestParam String[] names) {
        new ModelAndView("/home/list",[id:id, names: names])
    }



    @RequestMapping(path = '/add/{id}/product/{pid}')
    def add(@RequestParam() String id) {
        new ModelAndView('/home/list',[id:id])
    }
}
