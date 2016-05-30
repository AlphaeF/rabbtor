package com.rabbtor.model;

import com.rabbtor.model.annotation.Model;
import com.rabbtor.model.annotation.ModelName;

import java.util.Date;

@Model(name = "pilot")
public class PilotModel extends PersonModel
{
    @Override
    @ModelName(value = "Pilot Name")
    public String getName()
    {
        return super.getName();
    }

    @Override
    @ModelName(value = "Date of Birth", displayName = "dateofbirth")
    public Date getDateOfBirth()
    {
        return super.getDateOfBirth();
    }
}
