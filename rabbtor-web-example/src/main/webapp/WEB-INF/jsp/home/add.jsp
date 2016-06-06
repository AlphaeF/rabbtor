<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rform" uri="http://rabbytes.com/rabbtor/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <body>

        <form:form modelAttribute="registerCommand" >

            <p>
                <rform:label path="name" />
                <form:input path="name" />
                <form:errors path="name" />
            </p>

            <p>
                <rform:label path="department" />
                <form:input path="department" />
                <form:errors path="department" />
            </p>

            <h3>Addresses</h3>
            <c:forEach items="${registerCommand.addresses}" var="address" varStatus="i" >
                <h5>Address: ${i.index}</h5>
                <rform:label path="addresses[${i.index}].zipcode" />
                <form:input path="addresses[${i.index}].zipcode" />
                <form:errors path="addresses[${i.index}].zipcode" />
                <br />
            </c:forEach>


            <input type="submit" value="Submit" />
        </form:form>

    </body>
</html>