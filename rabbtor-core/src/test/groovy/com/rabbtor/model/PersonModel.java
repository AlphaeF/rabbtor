package com.rabbtor.model;


import com.rabbtor.model.annotation.Model;
import com.rabbtor.model.annotation.ModelName;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Model(value = "person")
public class PersonModel
{
    @ModelName(value = "name")
    private String name;

    @ModelName(value = "age")
    private int age;

    @ModelName(displayName = "personid")
    private Long id;

    private Map<String,AddressModel> addressMap;

    private AddressModel address;

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

    @ModelName(value = "getName")
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

    @ModelName(value ="id")
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

    public Map<String, AddressModel> getAddressMap()
    {
        return addressMap;
    }

    public void setAddressMap(Map<String, AddressModel> addressMap)
    {
        this.addressMap = addressMap;
    }

    public Double[] getRuns()
    {
        return runs;
    }

    public AddressModel getAddress()
    {
        return address;
    }

    public void setAddress(AddressModel address)
    {
        this.address = address;
    }
}
