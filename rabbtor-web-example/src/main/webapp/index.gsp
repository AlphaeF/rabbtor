<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <body>
        Hi
        <p>Request: ${request}</p>
        <p>Response: ${response}</p>
        <p>Session: ${session}</p>
        <p>Session: ${applicationContext}</p>

        <g:set var="name" scope="request" value="100" />

        <g:resource dir="css" file="test.css" />

        ${name}
    </body>
</html>