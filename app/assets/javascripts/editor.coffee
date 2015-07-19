define ['common', 'q', 'helpers/date', 'wysiwyg-editor.min'], (common, Q) -> {
  imageCount:1

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
          $("#editAlertRevision").show()
        count++
    ,(err) ->
      console.log "Could not receive list of revisions #{err}"
    result




setupEditorLeek: ()->
    require ['jquery.hotkeys'],() ->
      require ['bootstrap-wysiwyg'], () ->
        $("#editor").wysiwyg { activeToolbarClass:"btn-dob-toolbar",dragAndDropImages: false }
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
      result = Q.when jsRoutes.controllers.JsonApi.getPostRevisionById(id,revisionId).ajax({})
      result.then (data) ->
        $("#editor").html data.content
        $("#postId").val data.id
        $("#postTitle").text data.title
        $("#author").val data.author
        dt = data.dateCreated
        if typeof dt == "String"
          dt = dt.replace "BST", ""

        dte = new Date dt
        $("#dateCreated").val dte.yyyymmdd()

        if data.isDraft != false
          $("#editAlertDraft").show()
          $("#draft").val true
          $("#draftBtn").attr {class: "fa fa-power-off isDraftOn"}
        else
          $("#editAlertLive").show()
          $("#draft").val false
          $("#draftBtn").attr {class: "fa fa-power-off isDraftOff"}
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
    content = $('#editor').html()
    postType = $("#postType").val()
    id = $("#postId").val()
    author = $("#author").val()
    extraData = $("#extraDataValues").val()
    isDraft = $("#draftBtn").hasClass "isDraftOn"
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
    $("#save").attr {'class': 'fa fa-check-circle btnSuccessful'}
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
    $(d).text err.responseText
    $(d).attr { 'class':'alert alert-danger','role':'alert','id':'res-fail' }
    $("#result").html ""
    $("#result").append d
    $("#postId").val err
    $("#saveButton").hide()
    $("#btnFailure").show()
    $("#save").attr {'class': 'fa fa-times-circle btnFailure'}

  save: () ->
    postData = this.getContentData()
    data = postData.data
    if data.id == undefined || data.id == "-1" || data.id == -1
      result = Q.when jsRoutes.controllers.Authorised.submitPost().ajax postData
      result.then this.saveSucessfulHandler , this.saveFailedHandler
    else
      result = Q.when jsRoutes.controllers.Authorised.submitBlogUpdate().ajax postData
      result.then this.saveSucessfulHandler , this.saveFailedHandler



  draftModeToggle:()->
    isDraft = $("#draftBtn").hasClass("isDraftOn")
    if isDraft
      if $("#draft").val() == "true"
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").show()
      else
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").hide()
      $("#draftBtn").attr {class: "fa fa-power-off isDraftOff"}
    else
      if $("#draft").val() == "false"
        $("#editAlertLive2Draft").show()
        $("#editAlertDraft2Live").hide()
      else
        $("#editAlertLive2Draft").hide()
        $("#editAlertDraft2Live").hide()
      $("#draftBtn").attr {class: "fa fa-power-off isDraftOn"}

  unSavedChangesAlert:() ->
    $("#saveButton").show()
    $("#btnSuccessful").hide()
    $("#btnFailure").hide()
    $("#editAlertUnsaved").show()
    $("#save").attr {class: 'fa fa-save'}

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

  setupEditorBox :() ->
    self = this
    editor = $("#editor")
    this.preventEditorBlockQuote()
    $('#editor,#postTitle,#tagBox,#extraDataValues').on 'keyup', () ->
      self.unSavedChangesAlert()
    $(editor).on 'change', ()->
      self.unSavedChangesAlert()
    this.setupCustomScrollbar()
    this.editorDragDrop()

  setupCustomScrollbar:()->
    require ['jquery.mCustomScrollbar'], () ->
#$("#editor").mCustomScrollbar()

  warningBoxHandlers:()->
    $(".alert-close").on 'click', (event) ->
      $(event.target.parentNode).hide()

  preventEditorBlockQuote:()->
    $("#editor").on "DOMNodeInserted",(event) ->
      target = event.target

      switch target.nodeName
       when "BLOCKQUOTE"
          $(target).attr('style','margin: 0 0 0 40px; border: none; padding: 0px;')

  editorDragDrop:()->
    self = this
    editor = $("#editor")
    $(editor).attr {'ondragenter':'return false','ondragover':'return false'}
    $(editor).on 'drop',(event)->
      target = event.target or event.srcElement
      event.stopPropagation()
      event.preventDefault()
      self.uploadImage(event.originalEvent.dataTransfer.files, target.id)

  uploadImage:(files,target)->
    i = 0
    self = this
    f = undefined
    console.log target
    while f = files[i]
      imageReader = new FileReader
      imageReader.onload = ((aFile) ->
        (e) ->
          img = $ "<img>"
          imageId = "imageName" + self.imageCount
          self.imageCount++
          $(img).attr {'id':imageId,"class":'images','src':e.target.result}
          $("##{target}").append img
          result = Q.when $.ajax({
            url: "/upload",
            type: 'POST',
            processData: false,
            contentType: false,
            data: aFile
            })
          result.then (data) ->
              $("#result").html(data);
              $("#"+imageId).attr('src',"/"+data);
          , (error) ->
            $("#result").html(error);
      )(f)
      imageReader.readAsDataURL(f)
      i++

  loadTags:(id)->
    promise = Q.when jsRoutes.controllers.JsonApi.getContentTags(id).ajax({})
    promise.then (data) ->
      console.log "leek #{JSON.stringify(data)}"
      $.each data, (key,val) ->
        console.log "Loop #{val}"
        text = $("#tagBox").val()
        text = text + val
        text = text + ", ";

        $("#tagBox").val text

      text = $("#tagBox").val()
      text = text.replace /(^,)|(,$)|(, $)/g, ""
      $("#tagBox").val text

setupEditor: ()->
  self = this
  $("#editor").wysiwyg {
    toolbar: 'top-selection'
    buttons: {
      draft: {
        image: '<span id="draftBtn" class="fa fa-power-off isDraftOn"></span>'
        showselection: false
        click: ()->
          self.draftModeToggle()
      },
      save: {
        image: '<span id="save" class="fa fa-save"></span>',
        showstatic: true,
        click: () ->
          self.save()
      },

      bold: {
        title: "bold",
        image: '<span class="fa fa-bold"></span>',
        showselection: true
      },
      italic: {
        title: "italic",
        image: '<span class="fa fa-italic"></span>',
        showselection: true
      },
      underline: {
        title: "underline",
        image: '<span class="fa fa-underline"></span>',
        showselection: true
      },
      strikethrough: {
        title: "strikethrough",
        image: '<span class="fa fa-strikethrough"></span>',
        showselection: true
      },
      unorderedList: {
        title: "numbered list",
        image: '<span class="fa fa-list-ul"></span>',
        showselection: true
      },
      orderedList: {
        title: "bullet list",
        image: '<span class="fa fa-list-ol"></span>',
        showselection: true
      },
      indent: {
        title: "indent",
        image: '<span class="fa fa-indent"></span>',
        showselection: true
      },
      outdent: {
        title: "outdent",
        image: '<span class="fa fa-outdent"></span>',
        showselection: true
      },
      alignleft: {
        title: "align left",
        image: '<span class="fa fa-align-left"></span>',
        showselection: true
      },
      aligncenter: {
        title: "align center",
        image: '<span class="fa fa-align-center"></span>',
        showselection: true
      },
      alignright: {
        title: "align right",
        image: '<span class="fa fa-align-right"></span>',
        showselection: true
      },
      alignjustify: {
        title: "align justify",
        image: '<span class="fa fa-align-justify"></span>',
        showselection: true
      },
      insertlink: {
        title: "insert link",
        image: '<span class="fa fa-link"></span >',
        showselection: true
      },
      alias: {
        html: $('#aliasDrop').html()
        showselection: false
      },
      postType: {
        html: $('#typeDrop').html()
        showselection: false
      }


    }
  }
  this.addEditorMenu()
}
