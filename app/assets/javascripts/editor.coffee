define ['common','q','helpers/date'], (common,Q) -> {
  getRevisions:(id) ->
    self = this
    result = Q.when jsRoutes.controllers.JsonApi.getRevisionsWithDates(id).ajax({})
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
        revItem.on 'click', ()->
          self.loadContentPost $("#postId").val(), rev.commitId
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
    if id != -1 && id != "-1"
      console.log "id = #{JSON.stringify(id)}"
      result = Q.when jsRoutes.controllers.JsonApi.getPostRevisionById(id,revisionId).ajax({})
      result.then (data) ->
        $("#editor").html data.content
        $("#postId").val data.id
        $("#postTitle").text data.title
        $("#author").val data.author
        console.log "draft #{data.isDraft}"
        dt = data.dateCreated
        console.log typeof dt
        if typeof dt == "String"
          dt = dt.replace "BST", ""

        dte = new Date dt
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

  getContentData:()->
    dateStr = $("#dateCreated").val()
    title = $("#postTitle").text()
    content = $('#editor').cleanHtml()
    postType = $("#postType").val()
    id = $("#postId").val()
    author = $("#author").val()
    extraData = $("#extraDataValues").val()
    isDraft = $("#isDraft").hasClass "isDraftOn"
    tags = $("#tagBox").val()
    userId = $("#userId").val()
    json = {
      data: {
        "id": id,
        "dateCreated": dateStr,
        "title": title,
        "content": content,
        "author": author,
        "postType": postType,
        "filename": "",
        "extraData": extraData,
        "isDraft": isDraft,
        "tags": tags,
        "userId": userId
      },
      success:() ->
    }
    json

  saveSucessfulHandler:(data) ->
    d = $ '<div>'
    $(d).text "Saved"
    $(d).attr {'class':'alert alert-success','role':'alert','id':'res-success'}
    $("#result").html ""
    $("#postId").val data.id
    $("#saveButton").hide()
    $("#btnSuccessful").show()
    $("*[id*='editAlert']").hide()
    $("#draft").val data.isDraft
    $("#userId").val data.userId
    if data.isDraft != false
      $("#editAlertDraft").show()
    else
     $("#editAlertLive").show()
    this.getRevisions(data.id)

  saveFailedHandler:(err) ->
    d = $ '<div>'
    $(d).text "Save Failed" + JSON.stringify err
    $(d).attr { 'class':'alert alert-danger','role':'alert','id':'res-fail' }
    $("#result").html ""
    $("#result").append d
    $("#postId").val err
    $("#saveButton").hide()
    $("#btnFailure").show()

  save: () ->
    postData = this.getContentData()
    data = postData.data
    if data.id == undefined || data.id == "-1" || data.id == -1
      result = Q.when jsRoutes.controllers.Authorised.submitBlog().ajax postData
      result.then this.saveSucessfulHandler , this.saveFailedHandler
    else
      console.log JSON.stringify postData
      result = Q.when jsRoutes.controllers.Authorised.submitBlogUpdate().ajax postData
      result.then this.saveSucessfulHandler , this.saveFailedHandler

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

  unSavedChangesAlert:() ->
    $("#saveButton").show()
    $("#btnSuccessful").hide()
    $("#btnFailure").hide()
    $("#editAlertUnsaved").show()

  applyToolbarHandlers:()->
    self = this
    require ['jquery-ui'],() ->
      $("#isDraft"). on 'click', () ->
        self.draftModeToggle()
      toolbar = $('#toolbar')
      expanderIcon = $("#expanderIcon")
      $(toolbar).draggable { stack:"#editor" }

      $("#btnSuccessful").hide()
      $("#btnFailure").hide()

      $(toolbar).on 'dragstart', ()->
        $(toolbar).attr 'class','toolbar-compact'
        tbSpace = $("#tbSpace") #why this?
        $(tbSpace).hide()
        $(tbSpace).show()
        $(expanderIcon).attr 'class','fa fa-expand'

      $("#expander").on 'click' , () ->
        cls = $(expanderIcon).attr 'class'
        if cls == "fa fa-compress"
          $(toolbar).attr { "class":"toolbar-compact"}
          $(expanderIcon).attr 'class','fa fa-expand'
        else
          $(toolbar).attr { "class":"toolbar", 'style':''}
          $(expanderIcon).attr 'class','fa fa-compress'

      $("#saveButton").on 'click', () ->
        self.save()

}
