/**
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
