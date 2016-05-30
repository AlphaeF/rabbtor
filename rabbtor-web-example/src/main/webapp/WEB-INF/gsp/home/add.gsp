<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rbbform" uri="http://rabbtor.rabbytes.com/tags/form" %>
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


            <g:each in="${addCommand.addresses}" var="address" status="i" >
                <g:set var="path" value="${"addresses[$i].zipcode".toString()}" />
                <form:label path="${path}">
                    Test
                    ${rbbform:displayNameFor(path,pageContext)}
                </form:label>
                <form:input path="${path}" />
            </g:each>


            <input type="submit" value="Submit" />
        </form:form>

    </body>
</html>