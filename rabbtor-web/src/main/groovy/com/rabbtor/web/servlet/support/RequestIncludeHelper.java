
package com.rabbtor.web.servlet.support;


import org.springframework.core.convert.ConversionService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Cagatay Kalan
 * @since 4.3.0
 */
public class RequestIncludeHelper
{

    private ConversionService conversionService;
    private RequestParams params;
    private boolean includeRequestParams = false;

    public RequestIncludeHelper()
    {
        params = new RequestParams();
    }

    public ConversionService getConversionService()
    {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService)
    {
        this.conversionService = conversionService;
    }



    public boolean isIncludeRequestParams()
    {
        return includeRequestParams;
    }

    public void setIncludeRequestParams(boolean includeRequestParams)
    {
        this.includeRequestParams = includeRequestParams;
    }

    public RequestParams getParams()
    {
        return params;
    }

    public void setParams(RequestParams params)
    {
        this.params = params;
    }

    public IncludeResult include(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestIncludeWrapper wrappedRequest = new RequestIncludeWrapper(request,params.asRequestParameterMap(getConversionService()),includeRequestParams);
        ResponseIncludeWrapper wrappedResponse = new ResponseIncludeWrapper(response);
        include(path,wrappedRequest,wrappedResponse);
        return new DefaultIncludeResult(wrappedResponse);
    }

    public void include(String path, RequestIncludeWrapper wrappedRequest, ResponseIncludeWrapper wrappedResponse) throws ServletException, IOException
    {
        RequestDispatcher dispatcher = wrappedRequest.getRequest().getRequestDispatcher(path);
        dispatcher.include(wrappedRequest,wrappedResponse);
        wrappedResponse.flushBuffer();
    }

}
