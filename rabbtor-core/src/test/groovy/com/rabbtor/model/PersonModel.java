package com.rabbtor.model;


import com.rabbtor.model.annotation.Model;
import com.rabbtor.model.annotation.ModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Model(value = "person")
public class PersonModel
{
    @ModelProperty(displayName = "name")
    private String name;

    @ModelProperty(displayName = "age")
    private int age;

    @ModelProperty(displayNameKey = "personid")
    private Long id;

    private Date dateOfBirth;

    private Set<String> roles;
    private List<AddressModel> addresses;

    private Double[] runs;

    private int privateNum;
    protected int protectedNum;

    protected int getPrivateNum()
    {
        return privateNum;
    }

    @ModelProperty(displayName = "getName")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    @ModelProperty(displayName ="id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<String> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<String> roles)
    {
        this.roles = roles;
    }

    public List<AddressModel> getAddresses()
    {
        return addresses;
    }

    public Double[] getRuns()
    {
        return runs;
    }
}
