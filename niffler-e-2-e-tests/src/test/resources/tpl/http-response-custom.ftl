<#ftl output_format="HTML" auto_esc=true>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->

<html>
<head>
    <meta charset="UTF-8">
    <title>HTTP Response</title>

    <!-- Highlight.js from working Yandex source -->
    <link rel="stylesheet" href="https://yandex.st/highlightjs/8.0/styles/github.min.css">
    <script src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/xml.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/html.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>

    <style>
        body {
            padding: 15px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
        }
        .code-block {
            background-color: #f6f8fa;
            border-radius: 4px;
            padding: 10px;
            margin-bottom: 15px;
        }
        pre {
            margin: 0;
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
        }
        h4 {
            margin-top: 20px;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>

<div class="code-block">
    <h4>Status code</h4>
    <pre><code><b>${data.responseCode}</b></code></pre>
</div>

<#if data.url??>
    <div class="code-block">
        <h4>URL</h4>
        <pre><code>${data.url}</code></pre>
    </div>
</#if>

<#if data.body??>
    <div class="code-block">
        <h4>Body</h4>
        <#assign contentType = (data.headers?has_content && data.headers['Content-Type']??)?then(data.headers['Content-Type'], "")>
        <#assign lang = "plaintext">
        <#if contentType?lower_case?contains("application/json")>
            <#assign lang = "json">
        <#elseif contentType?lower_case?contains("application/xml")>
            <#assign lang = "xml">
        <#elseif contentType?lower_case?contains("text/html")>
            <#assign lang = "html">
        </#if>

        <#if lang == "json">
            <#attempt>
                <#assign parsed = data.body?eval>
                <#assign pretty = parsed?string("pretty")>
                <pre><code class="${lang}">${pretty}</code></pre>
            <#recover>
                <pre><code class="${lang}">${data.body}</code></pre>
            </#attempt>
        <#else>
            <pre><code class="${lang}">${data.body}</code></pre>
        </#if>
    </div>
</#if>

<#if data.headers?has_content>
    <div class="code-block">
        <h4>Headers</h4>
        <pre><code><#list data.headers as name, value><b>${name}</b>: ${value}<#sep>
</#list></code></pre>
    </div>
</#if>

<#if data.cookies?has_content>
    <div class="code-block">
        <h4>Cookies</h4>
        <pre><code class="json">{
<#list data.cookies as name, value>  "${name}": "${value}"<#sep>,
</#list>
}</code></pre>
    </div>
</#if>

</body>
</html>
