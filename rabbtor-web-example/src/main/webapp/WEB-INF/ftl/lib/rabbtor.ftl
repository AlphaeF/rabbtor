<#ftl strip_whitespace=true>

<#macro elem elemName attrs>
    <${elemName} <#list attrs?keys as key>${key}="${attrs[key]}" </#list> >
        <#nested >
    </${elemName}>
</#macro>