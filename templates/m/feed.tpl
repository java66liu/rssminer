<!doctype html>
<html>
  <head>
    {{>partials/m_header}}
    <link href="/s/css/m.css?{VERSION}" rel="stylesheet" type="text/css" />
  </head>
  <body>
    <h2>{{ title }}</h2>
    <p class="orginal">
      <a href="{{{link}}}" target="_blank">查看原文</a>
    </p>
    <div id="feed-content">
      <div class="summary">
        {{{summary}}}
      </div>
    </div>
  </body>
  <script>
    var links = document.getElementById('feed-content').querySelectorAll('a');
    for(var i = 0; i < links.length; i++) {
    var a = links[i];
    a.setAttribute('target', '_blank');
    }
  </script>
</html>
