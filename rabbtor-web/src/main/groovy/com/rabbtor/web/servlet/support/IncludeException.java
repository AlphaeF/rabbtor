
package com.rabbtor.web.servlet.support;


public class IncludeException extends RuntimeException
{


    public IncludeException(String message)
    {
        super(message);
    }

    public IncludeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
