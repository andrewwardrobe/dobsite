define ['common', 'q','jquery.mCustomScrollbar.concat.min'], (common, Q) -> {
  loadLatestPosts:()->
    self = this
    promise = Q.when jsRoutes.controllers.JsonApi.getContentByDateStart(1, "today", 10).ajax {}
    promise.then (data) ->
      self.attachPostsToDiv "#latestBlogs", data
    .done (data) ->
      $("#latestBlogs").mCustomScrollbar()

  attachPostsToDiv:(target,data)->
    $.each data, (idx, val) ->
      div = $("<div>")
      lnk = $("<a>").attr {href:"/post/"+val.id }
      lnk.text val.title
      div.append lnk
      $(target).append div
}






