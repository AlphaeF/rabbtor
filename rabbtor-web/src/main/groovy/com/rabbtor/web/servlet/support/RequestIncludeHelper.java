/*
 * Copyright 2002-2016 the original author or authors.
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

package com.rabbtor.web.servlet.support;


import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.RequestIncludeWrapper;

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

    public String include(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestIncludeWrapper wrappedRequest = new RequestIncludeWrapper(request,params.asRequestParameterMap(getConversionService()),includeRequestParams);
        ResponseIncludeWrapper wrappedResponse = new ResponseIncludeWrapper(response);
        include(path,wrappedRequest,wrappedResponse);
        return wrappedResponse.getContent();
    }

    public void include(String path, RequestIncludeWrapper wrappedRequest, ResponseIncludeWrapper wrappedResponse) throws ServletException, IOException
    {
        RequestDispatcher dispatcher = wrappedRequest.getRequest().getRequestDispatcher(path);
        dispatcher.include(wrappedRequest,wrappedResponse);
        wrappedResponse.flushBuffer();
    }

}
