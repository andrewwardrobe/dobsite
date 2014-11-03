doPager = (nxt, prev) ->
    if prev < 1
        prev = 1
    more = nxt + 4
    less = prev + 4
    $('#Next').attr 'onclick','doNewsWithClear(' +nxt+','+more+')'
    $('#Prev').attr 'onclick' ,'doNewsWithClear(' + prev + ','+less+')'

@doNews = (start, end) ->
	$.get '/json/news/' + start + '/'+ end, (data) ->
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
    doPager(end+1,start-5);

@doNewsWithClear = (start,end) ->
    $('#news').html ''
    doNews(start,end)

doNews(0,5)