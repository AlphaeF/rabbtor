package com.rabbtor.example.web.controllers

import com.rabbtor.example.web.models.EditPersonModel
import com.rabbtor.model.annotation.DisplayName
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.ModelAndView

import javax.validation.Valid
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
    def add(RegisterCommand cmd) {

        '/home/add'
    }

    @RequestMapping(path = '/add',method = RequestMethod.POST)
    def addPost(@Valid RegisterCommand cmd, BindingResult cmdErrors) {
        '/home/add'
    }
}

class RegisterCommand
{

    @NotEmpty
    @DisplayName("Name")
    String name

    @NotNull
    @DisplayName("Department")
    Long department

    @Valid
    List<AddressCommand> addresses = []

    RegisterCommand()
    {
        // Add one address to be displayed on form
        addresses.add( new AddressCommand())
    }
}

class AddressCommand
{

    @NotEmpty
    @DisplayName("Zipcode")
    String zipcode

}
