
package com.rabbtor.model;


import com.rabbtor.model.annotation.Model;


public class DriverModel extends PersonModel
{
    Double drivenMiles;

    public Double getDrivenMiles()
    {
        return drivenMiles;
    }

    public void setDrivenMiles(Double drivenMiles)
    {
        this.drivenMiles = drivenMiles;
    }
}
