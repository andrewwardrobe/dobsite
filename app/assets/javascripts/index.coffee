define ['common', 'q','jquery.mCustomScrollbar.concat.min'], (common, Q) -> {
  loadLatestPosts:()->
    promise = Q.when jsRoutes.controllers.JsonApi.getContentByDateStart(1, "today", 10).ajax {}
    promise.then (data) ->
      $.each data, (idx, val) ->
        div = $("<div>")
        div.text val.title
        $("#latestBlogs").append div
    .done (data) ->
      $("#latestBlogs").mCustomScrollbar()

}






