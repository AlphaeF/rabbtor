<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rbbform" uri="http://rabbtor.rabbytes.com/tags/form" %>
<html>
    <body>

        <form:form modelAttribute="registerCommand" >

            <rbbform:label path="name" />
            <form:input path="name" />
            <form:errors path="name" />

            <rbbform:label path="department" />
            <form:input path="department" />
            <form:errors path="department" />


            <g:each in="${registerCommand.addresses}" var="address" status="i" >
                <g:set var="path" value="${"addresses[$i].zipcode".toString()}" />
                <form:input path="${path}" />
            </g:each>


            <input type="submit" value="Submit" />
        </form:form>

    </body>
</html>