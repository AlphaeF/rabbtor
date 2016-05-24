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
}
