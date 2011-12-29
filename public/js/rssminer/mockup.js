(function () {

  function layout () {
    var width = $(window).width(),
        height = $(window).height(),
        nav_width = $('#navigation').width(),
        list_height = $('#footer').height();
    $("#navigation .wrapper").height(height - $("#admin-controls").height());
    $("#reading-area").height(height - list_height).width(width - nav_width);
  }

  $(window).resize(_.debounce(layout, 100));

  layout();

  function img_path (host) {
    return 'http://rssminer.net/fav?h=' + encodeURIComponent(host);
  }

  var subs = [{
    tag: 'java',
    list : [ {
      img:  img_path('blog.raek.se'),
      title: 'Higher-Order',
      like: 5,
      neutral: 7
    },{
      img: img_path('www.google.com'),
      title: 'Stuff Aria Likes',
      like: 3,
      neutral: 9,
      dislike: 3
    },{
      img: img_path('www.newsblur.com'),
      title: 'raek\'s blog',
      like: 13
    },{
      img: img_path('shenfeng.me'),
      title: 'The NewsBlur Blog'
    },{
      img: img_path('ibm.com'),
      title: 'IBM developerWorks : Java technology',
      like: 5,
      neutral: 1
    }]
  },{
    tag: 'Photoblogs',
    list: [{
      img: img_path('ifeng.com'),
      title: 'Brian Goetz\'s Oracle Blog',
      like: 41,
      neutral: 12
    },{
      img: img_path('pastebin.com'),
      title: 'Peter Norvig',
      like: 8,
      neutral: 93
    },{
      img: img_path('www.etao.com'),
      title: 'Planet Clojure',
      like: 5,
      neutral: 23
    },{
      img: img_path('www.360.cn'),
      title: 'Planet Clojure',
      like: 7,
      neutral: 45
    },{
      img: img_path('www.360buy.com'),
      title: 'the impossible cool.',
      like: 17,
      neutral: 18
    },{
      img: img_path('www.taobao.com'),
      title: 'Rands In Repose',
      like: 13,
      neutral: 7
    }]
  }, {
    tag: 'javascript',
    list: [{
      img: img_path('www.yahoo.com'),
      title:  'James Padolsey',
      like: 18,
      neutral: 18
    },{
      img: img_path('en.wikipedia.org'),
      title:  'BBC - Homepage',
      like: 31,
      neutral: 41
    },{
      img: img_path('www.bbc.co.uk'),
      title:  'Voice of America',
      like: 8,
      dislike: 1
    }, {
      img: img_path('www.qq.com'),
      title:  'Volunteers of America',
      like: 21,
      neutral: 9
    }, {
      img: img_path('www.voa.gov.uk'),
      title:  'VOA Associates Incorporated',
      like: 1
    }]
  }, {
    tag: 'linux',
    list : [ {
      img: img_path('news.baidu.com'),
      title: 'Higher-Order',
      like: 5,
      neutral: 7
    },{
      img: img_path('search.yahoo.com'),
      title: 'Stuff Aria Likes',
      like: 3,
      neutral: 9,
      dislike: 3
    },{
      img: img_path('www.weather.com.cn'),
      title: 'raek\'s blog',
      like: 13
    },{
      img: img_path('www.163.com'),
      title: 'The NewsBlur Blog'
    },{
      img: img_path('www.sohu.com'),
      title: 'IBM developerWorks : Java technology',
      like: 5,
      neutral: 1
    }]
  }];
  var html = Mustache.to_html(RM.tmpls.nav, {subs: subs});
  $("#navigation ul.sub-list").empty().append(html);

  var ymdate = RM.util.ymdate;

  var feeds = [{
    cls: 'unread like',
    tags: ['java', 'program'],
    author: 'Steve',
    title: 'Strachey\'s Checkers program from 1966',
    date: ymdate(new Date())
  }, {
    cls: 'unread like',
    author: 'feng',
    tags: ['apps', 'tech'],
    title: 'Introduction to Artificial Intelligence',
    date: ymdate(new Date())
  }, {
    cls: 'unread neutral',
    title: 'Dance Photography',
    date: ymdate(new Date())
  }, {
    tags: ['google', 'search engine'],
    cls: 'unread dislike',
    title: 'An Exercise in Species Barcoding',
    date: ymdate(new Date())
  }, {
    cls: 'unread dislike',
    title: 'Stomple: JMS via WebSockets',
    date: ymdate(new Date())
  }, {
    tags: ['win7', 'windows'],
    cls: 'read dislike',
    title: 'Stomple RC1: Combining WebSockets and Reliable Messaging',
    date: ymdate(new Date())
  }, {
    tags: ['nginx', 'linux'],
    cls: 'read like',
    author: 'Novig',
    title: 'Conj-labs Clojure lessons part i',
    date: ymdate(new Date())
  }, {
    tags: ['asp.net', 'mvc'],
    cls: 'read like',
    title: 'Clojure without the parentheses: looks a bit like ruby :)',
    date: ymdate(new Date())
  }, {
    cls: 'read like',
    title: 'ASP.NET 4.5 Series',
    date: ymdate(new Date())
  }, {
    tags: ['google', 'search engine'],
    cls: 'unread dislike',
    title: 'Let’s get this blog started again…',
    date: ymdate(new Date())
  }, {
    tags: ['java', 'async'],
    cls: 'unread neutral',
    title: 'June 26th Links: ASP.NET, ASP.NET MVC, .NET and NuGet',
    date: ymdate(new Date())
  }, {
    tags: ['web', 'html5'],
    cls: 'read like',
    title: 'Free “Guathon” all day event in London on June 6th',
    date: ymdate(new Date())
  }];

  html = Mustache.to_html(RM.tmpls.list, {feeds: feeds});
  $('#feed-list ul').empty().append(html);


  (function () {
    var down = false,
        startY,
        old_footer_height,
        old_list_height,
        $footer = $('#footer'),
        $list = $('#feed-list');

    function noop () { return false; }

    $(document).bind('mousedown', function (e) {
      var $target = $(e.target);
      if($target.hasClass('resizer') || $target.parents('.resizer').length) {
        startY = e.clientY;
        down = true;
        old_footer_height = $footer.height();
        old_list_height = $list.height();
        $(document).bind('selectstart', noop);
      }
    }).bind('mouseup', function (e) {
      $(document).unbind('selectstart', noop);
      down = false;
    }).bind('mousemove', function (e) {
      if(down) {
        var delta = e.clientY - startY;
        $footer.height(old_footer_height - delta);
        $list.height(old_list_height  - delta);
        layout();
      }
    });
  })();

  var $chooser = $('#reading-chooser li');
  $chooser.click(function () {
    $chooser.removeClass('selected');
    var $this = $(this);

    $this.addClass('selected');
    if($this.hasClass('iframe')) {
      $('#reading-area').addClass('show-iframe');
    } else {
      $('#reading-area').removeClass('show-iframe');
    }
  });

  $("#navigation .section h3").click(function (e) {
    // $(".section.active").removeClass('active');
    $(this).parents('.section').toggleClass('active');
  });

})();