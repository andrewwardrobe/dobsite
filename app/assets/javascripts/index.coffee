define ['common', 'q','jquery.mCustomScrollbar.concat.min'], (common, Q) -> {
  loadLatestPosts:()->
    self = this
    promise = Q.when jsRoutes.controllers.JsonApi.getContentByDateStart(1, "today", 15).ajax {}
    promise.then (data) ->
      self.attachPostsToDiv "#latestBlogs", data
    .done (data) ->
      $("#latestBlogs").mCustomScrollbar()

  loadNewsPosts:()->
    self = this
    promise = Q.when jsRoutes.controllers.JsonApi.getContentByDateStart(3, "today", 15).ajax {}
    promise.then (data) ->
      self.attachPostsToDiv "#latestNews", data
    .done (data) ->
      $("#latestNews").mCustomScrollbar()

  attachPostsToDiv:(target,data)->
    $.each data, (idx, val) ->
      div = $("<div>")
      lnk = $("<a>").attr {href:"/post/"+val._id.$oid}
      console.log JSON.stringify(val._id.$oid)
      lnk.text val.title
      div.append lnk
      $(target).append div
}






