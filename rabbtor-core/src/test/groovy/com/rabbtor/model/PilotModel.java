
package com.rabbtor.model;

import com.rabbtor.model.annotation.Model;
import com.rabbtor.model.annotation.DisplayName;

import java.util.Date;

@Model(name = "pilot")
public class PilotModel extends PersonModel
{
    @Override
    @DisplayName(value = "Pilot Name")
    public String getName()
    {
        return super.getName();
    }

    @Override
    @DisplayName(value = "Date of Birth", key = "dateofbirth")
    public Date getDateOfBirth()
    {
        return super.getDateOfBirth();
    }
}
