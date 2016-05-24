<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <body>
        <h1>Main Layout</h1>
        <g:layoutBody/>

        <g:ifPageProperty name="page.footScripts" >
            <g:pageProperty name="page.footScripts" />
        </g:ifPageProperty>
    </body>
</html>