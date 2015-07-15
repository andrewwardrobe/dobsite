define ['common', 'q', 'helpers/date'], (common, Q) -> {
setupClickHandler: (num) ->
  $("#pager").on 'click', ()->
    mode = $("#mode").val()
    author = ""
    if mode.split(":").length > 1
      author = mode.split(":")[1]
    date = $("#lastPostDate").val()
    promise = switch
      when mode == "all"
        Q.when jsRoutes.controllers.JsonApi.getContentByDateStart(1, date, num).ajax {}
      when /author:.*/.test(mode)
        Q.when jsRoutes.controllers.JsonApi.getContentByAuthorDateStart(author, 1, date, num).ajax {}

    promise.then (data) ->
      $.each data, (index, news) ->
        itm = $("<div>")
        $("#posts").append itm
        itm.attr 'id', 'itemId' + news.id
        itm.attr 'class', 'dob-post row'
        lnk = $("<a>")
        lnk.attr "href", "post/" + news.id
        lnk.attr "id", "itemLink" + news.id
        lnk.append $("<h2>", {class: 'dob-post-title'}).text news.title
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
        $("#lastPostDate").val dateStr
}






