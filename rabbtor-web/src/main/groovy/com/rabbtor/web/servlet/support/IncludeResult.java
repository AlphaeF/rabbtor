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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public abstract String getContentType();


    public String getContentOrEmpty() {
        try
        {
            return getContent();
        } catch (CharacterCodingException e)
        {
            return "";
        }
    }


    public void applyError(HttpServletResponse response) throws IOException
    {
        if (!isError())
            return;

        if (getStatusMessage() != null)
            response.sendError(getStatus(),getStatusMessage());
        else
            response.sendError(getStatus());

    }
}
