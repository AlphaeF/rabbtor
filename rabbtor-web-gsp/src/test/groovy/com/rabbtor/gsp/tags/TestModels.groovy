/*
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
package com.rabbtor.gsp.tags

import com.rabbtor.model.annotation.DisplayName
import com.rabbtor.model.annotation.Model

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Model("car")
class CarCommand
{
    @DisplayName("Car Id")
    Long id = 100
    int model = 2016

    String brand = '<b>BMW</b>'
    CarFactoryCommand factory = new CarFactoryCommand()
    Boolean produced = true
    Boolean onSale
    Boolean hasDiscount
    List<CarPartCommand> parts = []
    String[] orderLocations = []
    Date dateProduced = new Date()
    Double weight = 10.5


    UUID uniqueId = UUID.randomUUID()

}

class CarPartCommand
{
    @DisplayName("Part Number")
    Long id
    Date dateProduced = new Date()


}

class CarFactoryCommand
{
    Long id
    String name = '<b>Germany</b>'
    String email = "info@bmw.com"

    CarFactoryCommand()
    {
    }

    CarFactoryCommand(Long id, String name)
    {
        this.id = id
        this.name = name
    }


    @Override
    public String toString()
    {
        return "CarFactoryCommand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


class PersonCommand {

    @DisplayName("Account Number")
    String id;

    @DisplayName("Full Name")
    @NotNull
    String name;

    @Valid()
    @Size(min=1)
    List<AddressCommand> addresses

}


class AddressCommand {

    @NotNull
    String name


}
