package com.rabbtor.example.web.models

import com.rabbtor.model.annotation.Model
import com.rabbtor.model.annotation.ModelProperty
import org.springframework.web.bind.annotation.ModelAttribute

@Model(name="person")
class EditPersonModel
{
    @ModelProperty(displayName = "Name")
    String name
    Long id

}
