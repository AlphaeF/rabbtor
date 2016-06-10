<%--
  Created by IntelliJ IDEA.
  User: Cagatay
  Date: 8.05.2016
  Time: 01:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="r" uri="http://rabbytes.com/rabbtor/tags" %>
<%@ taglib prefix="rbbform" uri="http://rabbytes.com/rabbtor/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <title>Test</title>
    </head>
    <body>
        <p>Included:</p>
        <r:include path="/list/12" includeRequestParams="true">
            <s:param name="id">20</s:param>
            <s:param name="names">Foo</s:param>
            <s:param name="names">Bar</s:param>
        </r:include>
    </body>

    <jsp:include page="list.jsp" />
</html>
