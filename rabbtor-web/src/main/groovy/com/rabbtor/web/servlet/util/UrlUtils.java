
package com.rabbtor.web.servlet.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class UrlUtils
{
    public static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";

    public static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

    public static final String URL_TYPE_ABSOLUTE = "://";



    public static UrlType determineUrlType(String url) {
        if (!StringUtils.hasText(url))
            return UrlType.RELATIVE;
        if (url.contains(URL_TYPE_ABSOLUTE))
            return UrlType.ABSOLUTE;
        if (url.startsWith("/"))
            return UrlType.CONTEXT_RELATIVE;

        return UrlType.RELATIVE;

    }

}
