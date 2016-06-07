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
package com.rabbtor.web.servlet.jsp.tags;


import com.rabbtor.web.servlet.support.IncludeException;
import com.rabbtor.web.servlet.support.IncludeResult;
import com.rabbtor.web.servlet.support.IncludeStatusException;
import com.rabbtor.web.servlet.support.RequestIncludeHelper;
import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.ParamAware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSP tag for including the response of a web resource within another response (a.k.a server side includes )
 * Given the "path" of the resource to be included, an include request is sent and its response is captured
 * and displayed.
 * <p>
 * <p>Child request has its own request parameters which can be set in two ways:</p>
 * <ul>
 * <li>By setting the "params" attribute which requires a {@link Map < String , String []>}</li>
 * <li>By using one or more &lt;spring:param&gt; tags within this tag's body.</li>
 * </ul>
 * <p>
 * <p>
 * Multiple parameter values for the same parameter name can be provided using multiple &lt;spring:param&gt; tags
 * with the same parameter name.
 * </p>
 * <p>
 * <p>If {@code includeRequestParams} attribute is set to 'true', then current request parameters are merged with the
 * custom parameters provided to the child request.Child request parameters always override the parent request parameters.</p>
 * <p>
 * <p>Example usage:
 * <pre class="code">&lt;rabbtor:include path="/path-to-the-resource-to-be-included"&gt;
 * &lt;spring:param name="paramName" value="paramValue" /&gt;
 * &lt;spring:param name="anotherParamName" value="value1,value2,value3" /&gt;
 * &lt;/spring:url&gt;</pre>
 * </p>
 *
 * @author Cagatay Kalan
 * @see com.rabbtor.web.servlet.support.ResponseIncludeWrapper
 */
@SuppressWarnings("serial")
public class IncludeTag extends HtmlEscapingAwareTag implements ParamAware
{
    private Map<String, String[]> params;
    private String path;
    private Boolean includeRequestParams;
    private RequestIncludeHelper includeHelper;

    public void setParams(Map<String, String[]> params)
    {
        getParams().putAll(params);
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setIncludeRequestParams(boolean includeRequestParams)
    {
        this.includeRequestParams = includeRequestParams;
    }

    @Override
    public void addParam(Param param)
    {
        String value = param.getValue();
        String[] current = getParams().get(param.getName());
        if (current == null)
            current = new String[]{value};
        else
        {
            String[] old = current;
            current = new String[current.length + 1];
            System.arraycopy(old, 0, current, 0, old.length);
            current[current.length - 1] = value;
        }
        getParams().put(param.getName(), current);
    }

    @Override
    protected int doStartTagInternal() throws Exception
    {
        getParams();
        this.includeHelper = getIncludeHelper();
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException
    {
        Assert.notNull(path, "path parameter must be set.");
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

        try
        {
            if (includeRequestParams != null)
                this.includeHelper.setIncludeRequestParams(includeRequestParams);

            IncludeResult content = includeHelper.include(path, request, response);
            if (content.getRedirectUrl() != null)
            {
                response.sendRedirect(content.getRedirectUrl());
                return EVAL_PAGE;
            }
            else if (content.isError())
            {
                throw new IncludeStatusException(content);
            }

            pageContext.getOut().print(htmlEscape(content.getContent()));

        }
        catch (IncludeException e) {
            throw new JspException(e);
        }
        catch (ServletException e)
        {
            throw new JspException(e);
        } catch (IOException e)
        {
            throw new JspException(e);
        } finally
        {
            resetState();
        }

        return EVAL_PAGE;

    }

    private void resetState()
    {
        params = null;
        path = null;
        includeRequestParams = null;
    }

    protected RequestIncludeHelper getIncludeHelper()
    {
        return new RequestIncludeHelper();
    }

    protected Map<String, String[]> getParams()
    {
        if (params == null)
            params = new LinkedHashMap<String, String[]>();
        return params;
    }
}
