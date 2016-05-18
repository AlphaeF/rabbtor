package com.rabbtor.model;

import com.rabbtor.model.annotation.Model;
import com.rabbtor.model.annotation.ModelProperty;

import java.util.Date;

@Model(name = "pilot")
public class PilotModel extends PersonModel
{
    @Override
    @ModelProperty(displayName = "Pilot Name")
    public String getName()
    {
        return super.getName();
    }

    @Override
    @ModelProperty(displayName = "Date of Birth", displayNameKey = "dateofbirth")
    public Date getDateOfBirth()
    {
        return super.getDateOfBirth();
    }
}
