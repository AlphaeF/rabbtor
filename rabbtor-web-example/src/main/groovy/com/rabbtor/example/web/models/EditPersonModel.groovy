
package com.rabbtor.example.web.models

import com.rabbtor.model.annotation.Model
import com.rabbtor.model.annotation.DisplayName

@Model(name="person")
class EditPersonModel
{
    @DisplayName(value = "Name")
    String name
    Long id

}
