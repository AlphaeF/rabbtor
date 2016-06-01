package com.rabbtor.model;


public class UnsupportedModelException extends RuntimeException
{
    public UnsupportedModelException()
    {
    }

    public UnsupportedModelException(String message)
    {
        super(message);
    }

    public UnsupportedModelException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
