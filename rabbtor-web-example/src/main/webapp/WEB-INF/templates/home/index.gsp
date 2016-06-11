<%@ page import="org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder" contentType="text/html" %>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>

    <body>
        <g:form>
        </g:form>

        <g:form modelAttribute="cmd">
        </g:form>

        <g:form action="/" modelAttribute="cmd">

            <div class="checkbox">
                <label for="${g.idFor(path:'id')}">
                    <g:checkbox path="id" value="20"/> ${g.displayNameFor(path:'id')}
                </label>
            </div>
            <div class="checkbox">
                <label for="${g.idFor(path:'id')}">
                    <g:checkbox path="id" value="30"/> ${g.displayNameFor(path:'id')}
                </label>
            </div>

            <div class="radio">
                <label for="${g.idFor(path:'id')}">
                    <g:radio path="id" value="20"/> ${g.displayNameFor(path:'id')}
                </label>
            </div>
            <div class="radio">
                <label for="${g.idFor(path:'id')}">
                    <g:radio path="id" value="20"/> ${g.displayNameFor(path:'id')}
                </label>
            </div>
        </g:form>

        <g:form servletRelativeAction="/go" modelAttribute="cmd"></g:form>

        <g:form mvcUrl="[mapping: 'HC#list', urivars: [15], args:[['foo']]]" modelAttribute="cmd"></g:form>

        <g:form mvcUrl="[mapping: 'HC#list', urivars: [15]]" modelAttribute="cmd"></g:form>

    </body>
</html>

<content tag="footScripts">

</content>