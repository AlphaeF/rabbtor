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
package com.rabbtor.web.servlet.support;


import java.nio.charset.CharacterCodingException;

public class DefaultIncludeResult extends IncludeResult
{
    private ResponseIncludeWrapper response;

    public DefaultIncludeResult(ResponseIncludeWrapper response)
    {
        this.response = response;
    }

    @Override
    public String getContent() throws CharacterCodingException
    {
        return response.getContent();
    }

    @Override
    public int getStatus()
    {
        return response.getStatus();
    }

    @Override
    public String getStatusMessage()
    {
        return response.getStatusMessage();
    }

    @Override
    public boolean isError()
    {
        return response.hasError();
    }

    @Override
    public String getRedirectUrl()
    {
        return response.getRedirectUrl();
    }

    @Override
    public String getContentType()
    {
        return response.getContentType();
    }
}
