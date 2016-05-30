package com.rabbtor.model;


import com.rabbtor.model.annotation.ModelName;

public class AddressModel
{
    @ModelName
    String zipCode;

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }
}
