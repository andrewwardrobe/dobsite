@getRoles = () ->
	$.get "/userroles", (data) ->
		$.each data.pages, (index, page) ->
			item = $("<li>")
			link = $("<a>")
			link.text page
			link.attr 'href', jsRoutes.controllers.Authorised.newContent(page).url
			link.attr 'id','editLink' +page
			item.append link
			$("#editMenu").append item