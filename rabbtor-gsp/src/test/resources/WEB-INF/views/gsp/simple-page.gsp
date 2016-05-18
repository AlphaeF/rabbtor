<%@ page import="com.rabbtor.util.NameUtils" %>
<html>

    <body>
        <g:set var="totalMultiplied" value="${ total *2 }" />

        <g:each in="${people}" var="person">
            ${person.name}
        </g:each>

        <${ NameUtils.getGetterName('test')}
    </body>




</html>