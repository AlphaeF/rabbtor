<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rbbform" uri="http://rabbtor.rabbytes.com/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<form:form modelAttribute="addCommand" >

    <rbbform:label path="name" />
    <form:input path="name" />
    <form:errors path="name" />

    <rbbform:label path="id" />
    <form:input path="id" />
    <form:errors path="id" />

    <rbbform:label path="address.zipcode" />
    <form:input path="address.zipcode" />
    <form:errors path="address.zipcode" />




    <c:forEach items="${addCommand.addresses}" var="address" varStatus="i" >
        <rbbform:label path="addresses[${i.index}].zipcode" />
        <form:input path="addresses[${i.index}].zipcode" />
    </c:forEach>


    <input type="submit" value="Submit" />
</form:form>

</body>
</html>