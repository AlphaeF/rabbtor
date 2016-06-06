
package com.rabbtor.web.servlet.support;


public class IncludeStatusException extends IncludeException
{
    private IncludeResult includeResult;

    public IncludeStatusException(IncludeResult includeResult)
    {
        this("Unable to execute include.Response error. Status:[ " + includeResult.getStatus() + "]. Message: [" + includeResult.getStatusMessage() + "]", includeResult);
    }

    public IncludeStatusException(String message, IncludeResult includeResult)
    {
        super(message);
        this.includeResult = includeResult;
    }
}
