<%--
  Created by IntelliJ IDEA.
  User: Cagatay
  Date: 8.05.2016
  Time: 01:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<p>This is the list page.</p>
<ul>
    <c:forEach var="name" items="${names}">
        <li>${name}</li>
    </c:forEach>
</ul>
