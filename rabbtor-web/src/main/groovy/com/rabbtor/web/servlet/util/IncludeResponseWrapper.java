/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabbtor.web.servlet.util;


import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.CharacterCodingException;
import java.util.Locale;

/**
 * Response wrapper used to capture the content of a response (such as within in an include).
 *
 * @author Graeme Rocher
 * @since 1.2.1
 */
public class IncludeResponseWrapper extends HttpServletResponseWrapper
{


    private PrintWriter pw;
    private ByteArrayOutputStream os;
    private ServletOutputStream sos;
    private boolean usingStream;
    private boolean usingWriter;
    private int status;
    private boolean committed;

    public IncludeResponseWrapper(HttpServletResponse httpServletResponse)
    {
        super(httpServletResponse);
        os = new ByteArrayOutputStream();
        pw = new PrintWriter(os);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (usingWriter) throw new IllegalStateException("Method getWriter() already called");

        if (!usingStream)
        {
            usingStream = true;
            sos = new ServletOutputStream()
            {
                @Override
                public boolean isReady()
                {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener)
                {

                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException
                {
                    os.write(b, off, len);
                }

                @Override
                public void write(byte[] b) throws IOException
                {
                    os.write(b);
                }

                @Override
                public void write(int b) throws IOException
                {
                    os.write(b);
                }
            };
        }

        return sos;


    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        if (usingStream) throw new IllegalStateException("Method getOutputStream() already called");
        return pw;
    }

    public Object getContent() throws UnsupportedEncodingException
    {
        return getContent(getCharacterEncoding());
    }

    public Object getContent(String encoding) throws UnsupportedEncodingException
    {


        if (StringUtils.isEmpty(encoding))
            encoding = "ISO-8859-1";

        if (os != null)
            return os.toString(encoding);

        return "";
    }

    @Override
    public void resetBuffer()
    {
        os.reset();
    }

    @Override
    public void reset()
    {
        resetBuffer();
    }

    @Override
    public void flushBuffer()
    {
        if (pw != null)
            pw.flush();
        try
        {
            os.flush();
        } catch (IOException e)
        {
           throw new RuntimeException(e);
        }
    }
}
