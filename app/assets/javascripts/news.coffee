$ ->
	$.get "/json/news", (data) ->
		$.each data, (index, news) ->
            itm = $("<div>")
            $("#news").append itm
            itm.attr 'id', 'newsId'+ news.id
            itm.attr 'class', 'dob-post'
            itm.append $("<h2>", {class: 'dob-post-title'}).text news.title
            info = $("<p>")
            dte = new Date(news.dateCreated)
            info.text 'Posted On: ' + dte.toLocaleDateString() + ' ' + dte.toLocaleTimeString() + ' by ' + news.author
            itm.append info
            itm.append news.content