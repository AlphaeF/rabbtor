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

        <g:form action="/add" modelAttribute="cmd" ajax="[mode:'prepend']" id="cmdForm">

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

            <button type="submit" class="btn btn-primary">Submit</button>
        </g:form>

        <g:form action="~/go" modelAttribute="cmd"></g:form>

        <g:form action="[mapping: 'HC#list', urivars: [15], args:[['foo']]]" modelAttribute="cmd"></g:form>

        <g:form action="[mapping: 'HC#list', urivars: [15]]" modelAttribute="cmd"></g:form>

    </body>
</html>

<content tag="footScripts">
    <script type="text/javascript">
        $("#cmdForm").on('prepare.rbt.ajaxForm', function (e) {
            debugger;
        }).on('beforeSend.rbt.ajaxForm',function(e) {
            e.preventDefault();
            debugger;
        }).on('success.rbt.ajaxForm',function(e) {
            debugger;
        }).on('error.rbt.ajaxForm',function(e) {
            debugger;
        }).on('complete.rbt.ajaxForm',function(e) {
            debugger;
        });
    </script>
</content>