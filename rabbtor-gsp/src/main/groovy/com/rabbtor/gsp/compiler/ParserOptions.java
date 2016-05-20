package com.rabbtor.gsp.compiler;


import com.rabbtor.gsp.ParseTarget;
import com.rabbtor.taglib.encoder.OutputEncodingSettings;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;

public class ParserOptions
{
    ParseTarget target;
    String gspScriptSource;
    InputStream gspInputStream;
    String encoding;
    boolean developmentMode = false;
    OutputEncodingSettings outputEncodingSettings;
    File keepGeneratedFilesDirectory;
    boolean sitemeshPreprocessEnabled = true;
    String expressionCodecName;


    public ParserOptions(ParseTarget target, String gspScriptSource)
    {
        Assert.notNull(target,"target must not be null.");
        Assert.notNull(gspScriptSource,"gspScriptSource must not be null.");
        this.target = target;
        this.gspScriptSource = gspScriptSource;
    }

    public ParserOptions(ParseTarget target, InputStream gspInputStream)
    {

        this(target,gspInputStream,null);
    }

    public ParserOptions(ParseTarget target, InputStream gspInputStream, String encoding)
    {
        Assert.notNull(target,"target must not be null.");
        Assert.notNull(gspInputStream,"gspInputStream must not be null.");
        this.target = target;
        this.gspInputStream = gspInputStream;
        this.encoding = encoding;
    }

    public ParseTarget getTarget()
    {
        return target;
    }

    public String getGspScriptSource()
    {
        return gspScriptSource;
    }

    public InputStream getGspInputStream()
    {
        return gspInputStream;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public OutputEncodingSettings getOutputEncodingSettings()
    {
        return outputEncodingSettings;
    }

    public void setOutputEncodingSettings(OutputEncodingSettings outputEncodingSettings)
    {
        this.outputEncodingSettings = outputEncodingSettings;
    }

    public boolean isDevelopmentMode()
    {
        return developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode)
    {
        this.developmentMode = developmentMode;
    }

    public boolean isSitemeshPreprocessEnabled()
    {
        return sitemeshPreprocessEnabled;
    }

    public void setSitemeshPreprocessEnabled(boolean sitemeshPreprocessEnabled)
    {
        this.sitemeshPreprocessEnabled = sitemeshPreprocessEnabled;
    }

    public File getKeepGeneratedFilesDirectory()
    {
        return keepGeneratedFilesDirectory;
    }

    public void setKeepGeneratedFilesDirectory(File keepGeneratedFilesDirectory)
    {
        this.keepGeneratedFilesDirectory = keepGeneratedFilesDirectory;
    }

    public String getExpressionCodecName()
    {
        return expressionCodecName;
    }

    public void setExpressionCodecName(String expressionCodecName)
    {
        this.expressionCodecName = expressionCodecName;
    }


}
