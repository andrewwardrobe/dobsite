Date::yyyymmdd = ->

  pad2 = (n) ->
    # always returns a string
    (if n < 10 then '0' else '') + n

  @getFullYear() + pad2(@getMonth() + 1) + pad2(@getDate()) + pad2(@getHours()) + pad2(@getMinutes()) + pad2(@getSeconds())

@getPosts = (start,num,url) ->
    console.log("click")
    if window.loadingNews == 0 || window.loadingNews == undefined
        $('#R').attr 'class','btn btn-default'
        window.loadingNews = 1
        window.dataUrl = url
        $('#Prev').text 'Loading ...'
        doPosts(start,num)
        window.loadingNews = 0
        $('#Prev').text 'More'
        $('#Prev').attr 'class','btn btn-primary'

doPager = (nxt) ->
    $('#pager').unbind()
    $('#pager').on 'click', ->
        console.log("Jimmo")
        getPosts(nxt ,5,window.dataUrl)


@doPosts = (start, num) ->
      $.get window.dataUrl + '/'+ start + '/'+ num, (data) ->
        $.each data, (index, news) ->
            itm = $("<div>")
            $("#posts").append itm
            itm.attr 'id', 'newsId'+ news.id
            itm.attr 'class', 'dob-post row'
            lnk = $("<a>")
            lnk.attr "href", "post/" + news.id
            lnk.attr "id", "newsLink" + news.id
            lnk.append  $("<h2>", {class: 'dob-post-title'}).text news.title
            itm.append lnk
            typ = $("<hidden>")
            typ.attr 'id', 'typId' + news.id
            typ.attr 'value', news.postType
            itm.append typ
            info = $("<div>")
            dte = new Date(news.dateCreated)
            dateStr = dte.yyyymmdd()
            info.text 'Posted On: ' + dte.toLocaleDateString() + ' by ' + news.author
            itm.append info
            cnt = $("<div>")
            cnt.attr 'id', 'content'
            cnt.append news.content
            itm.append cnt
            next = news.id - 1
            doPager(dateStr);







