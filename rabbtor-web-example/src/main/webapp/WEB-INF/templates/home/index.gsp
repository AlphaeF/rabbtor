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




        <g:form name="frm" id="frm" modelAttribute="cmd" style="background: #eee; height: 40px" >
            <p>
            <g:label path="name" />
            <g:input path="name" type="text" disabled="" readonly="" />
            </p>

            <p>
                <g:label path="dateOfBirth" ></g:label>
                <g:input path="dateOfBirth" />
            </p>

            <p>
                <g:label path="price" ></g:label>
                <g:input path="price"  />
            </p>

            <p>
                <g:label path="weight" ></g:label>
                <g:input path="weight"  />
            </p>

            <p>
                <% cmd.id = 15 %>
            <g:label path="id" ></g:label>
            <g:select path="id" items="${cmd.data}" itemValue="id" itemLabel="name" noSelection="['':'-Please Select-']"  >
            </g:select>
            </p>
        </g:form>


    </body>
</html>

<content tag="footScripts">
    Foot scripts of index.gsp
</content>