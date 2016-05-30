<%@ page import="org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder" contentType="text/html" %>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>

    <body>
        Hi baby test ewewrer

        <p>
            Mvc Url: <g:mvcUrl mapping="HC#list" urivars="[15, 20]"/>
        </p>

        <p>
            Mvc path:
            <g:mvcPath mapping="HC#list"  urivars="[15, 20]"/>
        </p>

        <p>
            Mvc Include:

        <p>
            <g:include mapping="HC#list" urivars="[15]" params="[id:10,names:['foo','bar']]" />
        </p>

        <my:testSet var="size" value="100" />
        ${size}

            <g:elm tagName="div" id="100" name="test" class="{15}" >
                <g:elm tagName="span" >Hi</g:elm>
            </g:elm>

            <g:formatDate date="${new Date()}" />

            <g:message code="test.me" />
    </p>






    </body>
</html>

<content tag="footScripts">
    Foot scripts of index.gsp
</content>