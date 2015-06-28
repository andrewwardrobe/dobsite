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
    require ['jquery.hotkeys'],() ->
      require ['bootstrap-wysiwyg'], () ->
        $("#editor").wysiwyg { activeToolbarClass:"btn-dob-toolbar" }
    this.addEditorMenu()

  addEditorMenu:()->
    edFuncLink = $ "<a>"
    $(edFuncLink).attr { 'id':"editorMenu", 'href':'#','class':"dropdown-toggle",'data-toggle':"dropdown" }
    $(edFuncLink).html 'Revisions<span class="caret"></span>'
    listElem = $ "<li>"
    $(listElem).append edFuncLink
    $(listElem).attr 'class','dropdown'
    edFuncMenu = $ "<ul>"
    $(edFuncMenu).attr {'class':'dropdown-menu','role':"menu",'id':"revisions" }
    $(listElem).append edFuncMenu
    $("#rightSideNavBar").prepend listElem

  loadContentPost:(id,revisionId) ->
    self = this
    if id != "-1"
      result = Q.when jsRoutes.controllers.JsonApi.getPostRevisionById(id,revisionId).ajax({})
      result.then (data) ->
        $("#editor").html data.content
        $("#postId").val data.id
        $("#postTitle").text data.title
        $("#author").val data.author
        dte = new Date data.dateCreated
        $("#dateCreated").val dte.yyyymmddDashes()
        if data.isDraft != false
          $("#editAlertDraft").show()
          $("#draft").val true
          self.applyDraftButtonCss true
        else
          $("#editAlertLive").show()
          $("#draft").val false
          self.applyDraftButtonCss false
        extraData = JSON.parse data.extraData
        text = ""
        for key in extraData
          if extraData.hasOwnProperty key
            text += "#{key}=#{extraData[key]}"
        text.replace "\n$", ""
        $("#extraDataValues").val text
    else
      $("#editAlertNew").show()

  applyDraftButtonCss:(isDraft)->
    btn = $("#isDraft")
    if isDraft
      $(btn).removeClass "isDraftOff"
      $(btn).addClass "isDraftOn"
      $("#isDraft").attr 'title','This post is Draft'
    else
      $(btn).removeClass "isDraftOn"
      $(btn).addClass "isDraftOff"
      $("#isDraft").attr 'title','This post is Live'

  draftModeToggle:()->
    isDraft = $("#isDraft").hasClass("isDraftOn")
    if isDraft
      if $("#draft").val() == "true"
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").show()
      else
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").hide()
      this.applyDraftButtonCss(false)
    else
      if $("#draft").val() == "false"
        $("#editAlertLive2Draft").show()
        $("#editAlertDraft2Live").hide()
      else
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").hide()
      this.applyDraftButtonCss(true)

}
