<%--

    Copyright 2016 - Rabbytes Incorporated
    All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Rabbytes Incorporated and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Rabbytes Incorporated
    and its suppliers and may be covered by U.S. and Foreign Patents,
    patents in process, and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Rabbytes Incorporated.

--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title></title>
    </head>

    <body>
        <g:form modelAttribute="carCommand">
            <div id="inputs">
            <g:hidden type="text" path="id" />

            <g:input type="text" path="brand" class="cls" data-id="id" />
            <g:input type="password" path="factory.name" class="cls" data-id="id"/>
            <g:input type="email" path="factory.email" class="cls" data-id="id"/>

            <g:checkbox path="produced" class="cls" data-id="id"/>
            <g:checkbox path="produced" value="true" class="cls" data-id="id" />
            <g:checkbox path="produced" value="false" class="cls" data-id="id" />

            <g:radio path="brand" value="${carCommand.brand}" class="cls" data-id="id" />
            <g:radio path="produced" value="true" class="cls" data-id="id" />
            <g:radio path="dateProduced" value="${carCommand.dateProduced}" class="cls" data-id="id" />

            </div>

            <g:textarea path="factory.name" class="cls" data-id="id" />

            <div id="parts">
                <g:each in="${carCommand.parts}" status="i" var="part" >
                    <g:input type="text" path="parts[${i}].id" class="cls" data-id="id" />
                    <g:checkbox path="parts[${i}].id" value="${part.id}" class="cls" data-id="id" />
                </g:each>
            </div>

        </g:form>
    </body>
</html>