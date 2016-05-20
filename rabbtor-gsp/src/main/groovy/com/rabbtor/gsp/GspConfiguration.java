package com.rabbtor.gsp;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class GspConfiguration
{
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Value("${spring.gsp.sitemesh.preprocess:false}")
    private boolean sitemeshPreprocessEnabled = false;

    @Value("${spring.gsp.encoding:UTF-8}")
    private String encoding = DEFAULT_ENCODING;

    @Value("${spring.gsp.reload:false}")
    private boolean reloadEnabled;

    @Value("${spring.gsp.cache:true}")
    private boolean cacheResources = true;

    @Value("${spring.gsp.keepGeneratedFilesPath:#{null}}")
    private String keepGeneratedFilesDirectory;

    @Value("${spring.gsp.templateRoots:#{null}}")
    private String[] templateRoots;




    public boolean isCacheResources()
    {
        return cacheResources;
    }

    public void setCacheResources(boolean cacheResources)
    {

        this.cacheResources = cacheResources;
    }


    public boolean isSitemeshPreprocessEnabled()
    {
        return sitemeshPreprocessEnabled;
    }

    public void setSitemeshPreprocessEnabled(boolean sitemeshPreprocessEnabled)
    {
        this.sitemeshPreprocessEnabled = sitemeshPreprocessEnabled;
    }





    public String getEncoding()
    {

        return encoding;
    }

    public void setEncoding(String encoding)
    {
        Assert.hasText(encoding, "encoding must not be empty.");
        this.encoding = encoding;
    }

    public boolean isReloadEnabled()
    {

        return reloadEnabled;
    }

    public void setReloadEnabled(boolean reloadEnabled)
    {
        this.reloadEnabled = reloadEnabled;
    }

    public String getKeepGeneratedFilesDirectory()
    {

        return keepGeneratedFilesDirectory;
    }

    public void setKeepGeneratedFilesDirectory(String keepGeneratedFilesDirectory)
    {
        this.keepGeneratedFilesDirectory = keepGeneratedFilesDirectory;
    }

    public String[] getTemplateRoots()
    {
        return templateRoots;
    }

    public void setTemplateRoots(String[] templateRoots)
    {
        this.templateRoots = templateRoots;
    }

}
