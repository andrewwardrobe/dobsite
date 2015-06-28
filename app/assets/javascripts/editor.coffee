define ['common','q','helpers/date'], (common,Q) -> {
  getRevisions:(id) ->
    self = this
    result = Q.when jsRoutes.controllers.JsonApi.getRevisionsWithDates(1).ajax({})
    result.then (data) ->
      count = 1
      revisions = $("#revisions")
      revisions.html ""
      $.each data, (idx,rev) ->
        dte = new Date(rev.commitDate.replace("BST",""));
        revItem = $("<li>")
        revItem.attr 'id', "revId#{count}"
        link = $("<a>")
        link.attr {'id': "revLink#{count}", 'href':"#"}
        link.text dte.DTString()
        revItem.append link
        revisions.append revItem
      count++
    ,(err) ->
      console.log "Could not receive list of revisions #{err}"
    result

  setupEditor:()->
    require ['javascripts/bootstrap-wysiwyg'], ()->
      $("#editor").wysiwyg()

}