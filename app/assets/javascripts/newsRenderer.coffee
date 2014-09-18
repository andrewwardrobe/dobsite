$ ->
	$.get "/news", (data) ->
		$.each data, (index, news) ->
			$("#news").append $("<div>", {id: 'news'+ news.id, class: 'newsClass'})
			$("#news" +news.id).append $("<h2>").text news.title 
			$("#news" +news.id).append news.content