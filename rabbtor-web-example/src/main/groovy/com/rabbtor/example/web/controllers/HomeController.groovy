package com.rabbtor.example.web.controllers

import com.rabbtor.example.web.models.EditPersonModel
import com.rabbtor.model.annotation.ModelName
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.servlet.ModelAndView

import javax.validation.Valid
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.constraints.NotNull

@Controller
@RequestMapping(path = '/')
class HomeController
{

//    @Autowired
//    Validator validator

    @RequestMapping(path = '/')
    def index(ServletWebRequest webRequest) {
        new ModelAndView('/home/index',
                [id:20, cmd: new EditPersonModel()  ])
    }

    @RequestMapping(path = '/list/{id}')
    def list(@RequestParam(required = false) String[] names) {
        new ModelAndView("/home/list",[names: names])
    }


    @RequestMapping(path='/add',method=RequestMethod.GET)
    def add(AddCommand cmd) {

        '/home/add'
    }

    @RequestMapping(path = '/add',method = RequestMethod.POST)
    def addPost(@Valid AddCommand cmd, BindingResult cmdErrors) {
        '/home/add'
    }
}

class AddCommand {

    @NotNull
    String name

    @NotNull
    @ModelName(displayName = "Person Id")
    Long id

    @Valid
    AddresCommand address

    List<AddresCommand> addresses = [new AddresCommand(),new AddresCommand()]

}

class AddresCommand {

    @NotEmpty
    @ModelName(displayName = "Zip Code")
    String zipcode

}
