package com.rabbtor.web.servlet.support;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;

public abstract class IncludeResult
{
    private static Log LOG = LogFactory.getLog(IncludeResult.class);

    public abstract String getContent() throws CharacterCodingException;
    public abstract int getStatus();
    public abstract String getStatusMessage();
    public abstract boolean isError();
    public abstract String getRedirectUrl();


    public String getContentOrEmpty() {
        try
        {
            return getContent();
        } catch (CharacterCodingException e)
        {
            return "";
        }
    }


}
