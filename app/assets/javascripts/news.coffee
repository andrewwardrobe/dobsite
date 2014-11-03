doPager = (nxt) ->
    if nxt < 0
        nxt = 0
    $('#Prev').attr 'onclick','doNews(' +nxt+',5)'


@doNews = (start, num) ->
	  $.get '/json/news/' + start + '/'+ num, (data) ->
        $.each data, (index, news) ->
            itm = $("<div>")
            $("#news").append itm
            itm.attr 'id', 'newsId'+ news.id
            itm.attr 'class', 'dob-post'
            itm.append $("<h2>", {class: 'dob-post-title'}).text news.title
            typ = $("<hidden>")
            typ.attr 'id', 'typId' + news.id
            typ.attr 'value', news.postType
            itm.append typ
            info = $("<div>")
            dte = new Date(news.dateCreated)
            info.text 'Posted On: ' + dte.toLocaleDateString() + ' ' + dte.toLocaleTimeString() + ' by ' + news.author
            itm.append info
            itm.append news.content
            next = news.id - 1
            doPager(next);

@doNewsWithClear = (start,end) ->
    $('#news').html ''
    doNews(start,end)


doNews(-1,5)