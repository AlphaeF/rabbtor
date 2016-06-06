<%--
  Created by IntelliJ IDEA.
  User: Cagatay
  Date: 8.05.2016
  Time: 01:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="rbb" uri="http://rabbytes.com/rabbtor/tags" %>
<%@ taglib prefix="rbbform" uri="http://rabbytes.com/rabbtor/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <title>Test</title>
    </head>
    <body>
        <p>Included:</p>
        <rbb:include path="/list" includeRequestParams="true">
            <s:param name="id">20</s:param>
            <s:param name="names">Çağatay,Ali,Ahmet</s:param>
        </rbb:include>

        ${rbbform:propertyDisplayName("",pageContext)}
    </body>
</html>
