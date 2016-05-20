package com.rabbtor.gsp;


import org.springframework.util.Assert;

public class ParseTarget
{
    private String name;
    private String uri;
    private String fileName;

    public ParseTarget(String name, String uri, String fileName)
    {
        Assert.hasText(name,"name must not be empty.");
        Assert.hasText(uri,"uri must not be empty.");
        Assert.hasText(fileName,"fileName must not be empty.");
        this.name = name;
        this.uri = uri;
        this.fileName = fileName;
    }

    public String getName()
    {
        return name;
    }

    public String getUri()
    {
        return uri;
    }

    public String getFileName()
    {
        return fileName;
    }
}
