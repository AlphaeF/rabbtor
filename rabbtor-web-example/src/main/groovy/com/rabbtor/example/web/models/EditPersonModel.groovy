package com.rabbtor.example.web.models

import com.rabbtor.model.annotation.Model
import com.rabbtor.model.annotation.ModelName

@Model(name="person")
class EditPersonModel
{
    @ModelName(value = "Name")
    String name
    Long id

}
