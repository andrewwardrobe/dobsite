doPager = (nxt) ->
    if nxt < 0
        nxt = 0
    $('#Prev').attr 'onclick','doNewsItems(' +nxt+',5)'


@doNews = (start, num) ->
	  $.get '/json/news/' + start + '/'+ num, (data) ->
        $.each data, (index, news) ->
            itm = $("<div>")
            $("#news").append itm
            itm.attr 'id', 'newsId'+ news.id
            itm.attr 'class', 'dob-post row'
            itm.append $("<h2>", {class: 'dob-post-title'}).text news.title
            typ = $("<hidden>")
            typ.attr 'id', 'typId' + news.id
            typ.attr 'value', news.postType
            itm.append typ
            info = $("<div>")
            dte = new Date(news.dateCreated)
            info.text 'Posted On: ' + dte.toLocaleDateString() + ' by ' + news.author
            itm.append info
            cnt = $("<div>")
            cnt.attr 'id', 'content'
            cnt.append news.content
            itm.append cnt
            next = news.id - 1
            doPager(next);

@doNewsWithClear = (start,end) ->
    $('#news').html ''
    doNews(start,end)


@doNewsItems = (start,num) ->
    if window.loadingNews == 0
        $('#Prev').attr 'class','btn btn-default'
        window.loadingNews = 1
        $('#Prev').text 'Loading ...'
        doNews(start,num)
        window.loadingNews = 0
        $('#Prev').text 'More'
        $('#Prev').attr 'class','btn btn-primary'

window.loadingNews = 0
doNewsItems(-1,5)