<!DOCTYPE html>
<#import "/spring.ftl" as spring />
<#import "../lib/rabbtor.ftl" as rabbtor />
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Home</title>
    </head>
    <body>
        <@rabbtor.elem "div" {"id":"id", "name":"foo"} >
            Hi
        </@rabbtor.elem>
    </body>
</html>