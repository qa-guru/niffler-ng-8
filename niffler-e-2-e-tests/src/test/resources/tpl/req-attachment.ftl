<!DOCTYPE html>
<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>HTTP Request</title>

    <link rel="stylesheet" href="https://yastatic.net/bootstrap/3.3.6/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="https://yandex.st/highlightjs/8.0/styles/github.min.css">

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
            white-space: pre-wrap;
            word-break: break-word;
            margin: 0;
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
        }
        h4.collapse-header {
            margin-top: 20px;
            margin-bottom: 10px;
            cursor: pointer;
            padding: 5px 10px;
            background: #f0f0f0;
            border-radius: 4px;
        }
        .collapse-icon {
            transition: transform 0.2s;
            display: inline-block;
        }
        .collapse-header:not(.collapsed) .collapse-icon {
            transform: rotate(180deg);
        }
    </style>
</head>
<body>
    <div class="code-block">
        <pre><code><#if data.method??>${data.method?html}<#else>GET</#if>: <#if data.url??>${data.url?html}<#else>Unknown</#if></code></pre>
    </div>

    <#if data.body??>
    <h4 class="collapse-header collapsed" data-toggle="collapse" data-target="#body-section">
        Body <span class="collapse-icon">▲</span>
    </h4>
    <div class="code-block collapse" id="body-section">
        <pre><code class="language-${data.bodyType!'text'}">${data.body?html}</code></pre>
    </div>
    </#if>

    <#if (data.headers)?has_content>
    <h4 class="collapse-header collapsed" data-toggle="collapse" data-target="#headers-section">
        Headers <span class="collapse-icon">▲</span>
    </h4>
    <div class="code-block collapse" id="headers-section">
        <pre><code><#list data.headers as name, value><b>${name?html}</b>: ${value?html}<#sep>&#10;</#list></code></pre>
    </div>
    </#if>

    <#if (data.cookies)?has_content>
    <h4 class="collapse-header collapsed" data-toggle="collapse" data-target="#cookies-section">
        Cookies <span class="collapse-icon">▲</span>
    </h4>
    <div class="code-block collapse" id="cookies-section">
        <pre><code><#list data.cookies as name, value><b>${name?html}</b>: ${value?html}<#sep>&#10;</#list></code></pre>
    </div>
    </#if>

    <#if data.curl??>
    <h4 class="collapse-header collapsed" data-toggle="collapse" data-target="#curl-section">
        cURL <span class="collapse-icon">▲</span>
    </h4>
    <div class="code-block collapse" id="curl-section">
        <pre><code class="language-bash">${data.curl?html}</code></pre>
    </div>
    </#if>

    <script src="https://yastatic.net/jquery/2.2.3/jquery.min.js" crossorigin="anonymous"></script>
    <script src="https://yastatic.net/bootstrap/3.3.6/js/bootstrap.min.js" crossorigin="anonymous"></script>
    <script src="https://yandex.st/highlightjs/8.0/highlight.min.js" crossorigin="anonymous"></script>
    <script>
        $(document).ready(function() {
            hljs.configure({languages: ['bash', 'json', 'xml']});
            hljs.initHighlighting();

            $('.collapse').on('show.bs.collapse', function() {
                $(this).prev('.collapse-header').removeClass('collapsed');
            }).on('hide.bs.collapse', function() {
                $(this).prev('.collapse-header').addClass('collapsed');
            });

            $('.collapse').on('shown.bs.collapse', function() {
                $(this).find('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
            });
        });
    </script>
</body>
</html>