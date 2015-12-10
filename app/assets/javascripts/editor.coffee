define ['common', 'q', 'utilities','helpers/date','highlight.pack','wysiwyg','wysiwyg-editor'], (common, Q, utils ) -> {
  imageCount:1

  # http://stackoverflow.com/questions/6690752/insert-html-at-caret-in-a-contenteditable-div
  pasteHtmlAtCaret: (html) ->
    #console.log "in func"
    sel = undefined
    range = undefined
    if window.getSelection
  # IE9 and non-IE
      sel = window.getSelection()
      if sel.getRangeAt and sel.rangeCount
        range = sel.getRangeAt(0)
        range.deleteContents()
        # Range.createContextualFragment() would be useful here but is
        # non-standard and not supported in all browsers (IE9, for one)
        el = document.createElement('div')
        el.innerHTML = html
        frag = document.createDocumentFragment()
        node = undefined
        lastNode = undefined
        while node = el.firstChild
          lastNode = frag.appendChild(node)
        range.insertNode frag
        # Preserve the selection
        if lastNode
          range = range.cloneRange()
          range.setStartAfter lastNode
          range.collapse true
          sel.removeAllRanges()
          sel.addRange range

    else if document.selection and document.selection.type != 'Control'
  # IE < 9
      document.selection.createRange().pasteHTML html
    return undefined

  getRevisions:(id) ->
    #console.log "Getting Revisions id = #{id}"
    self = this
    result = Q.when jsRoutes.controllers.JsonApi.getRevisionsWithDates(id).ajax({})
    result.then (data) ->
      if data != "None"
        #console.log "Found Revisions= #{JSON.stringify data}"
        count = 1
        revisions = $("#revisions")
        revisions.html ""
        $.each data, (idx,rev) ->
          dte = new Date(rev.commitDate.replace("BST", ""))
          #console.log "leek"
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
      else
        #console.log "No Revisions"
    ,(err) ->
      #console.log "Could not receive list of revisions #{err}"
    result




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
    newPost = $("#newPost").val()
    if newPost != "true"
      #console.log "id  #{id}"
      result = Q.when jsRoutes.controllers.JsonApi.getPostRevisionById(id,revisionId).ajax({})
      result.then (data) ->
        console.log "loading... "
        #console.log "data = #{JSON.stringify data} "
        $("#editor").html data.content
        #$("#postId").val data._id.stringify
        $("#postTitle").text data.title
        $("#author").val data.author
        if data.userId != undefined
          $("#userId").val data.userId.$oid
        console.log("Date shit")
        dt = data.dateCreated
        if typeof dt == "String"
          dt = dt.replace "BST", ""


        dte = new Date dt
        $("#dateCreated").val dte.yyyymmdd()
        console.log("done date stuff")
        if data.isDraft != false
          $("#editAlertDraft").show()
          $("#draft").val true
          $("#draftBtn").attr {class: "fa fa-power-off isDraftOn"}
        else
          $("#editAlertLive").show()
          $("#draft").val false
          $("#draftBtn").attr {class: "fa fa-power-off isDraftOff"}
        extraData = data.extraData
        text = ""
        console.log("expanding extra data")
        for key in Object.keys(extraData)
          console.log("Key = key")
          if extraData.hasOwnProperty key
            text += "#{key}=#{extraData[key]}\n"
            if key == "thumb"
              $("#bioImage").attr 'src', "/#{extraData[key]}"
        text.replace "\n$", ""
        $("#extraDataValues").val text
        $("pre code").each (i, blk) ->
          $(blk).on 'click', (event) ->
            event.stopPropagation()
          hljs.highlightBlock blk
      ,(err) ->
        #console.log err
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
        "_id": id,
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
    }
    json

  saveSucessfulHandler:(data) ->
    d = $ '<div>'
    $(d).text "Saved"
    $(d).attr {'class':'alert alert-success','role':'alert','id':'res-success'}
    $("#result").html ""
    $("#postId").val data._id.$oid
    $("#saveButton").hide()
    $("#btnSuccessful").show()
    $("#save").attr {'class': 'fa fa-check-circle btnSuccessful'}
    $("*[id*='editAlert']").hide()
    $("#draft").val data.isDraft
    $("#userId").val data.userId.$oid
    $("#newPost").val "false"
    #console.log("Saved")
    if data.isDraft != false
      $("#editAlertDraft").show()
    else
     $("#editAlertLive").show()
    #console.log data._id.$oid
    jsRoutes.controllers.JsonApi.getRevisionsWithDates(data._id.$oid).ajax({
      success:(data) ->
        #console.log "Leeeeeeeeeeeeeeeek"
        if data != "None"
          #console.log "Found Revisions= #{JSON.stringify data}"
          count = 1
          revisions = $("#revisions")
          revisions.html ""
          $.each data, (idx,rev) ->
            dte = new Date(rev.commitDate.replace("BST", ""))
            #console.log "leek"
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
        else
          #console.log "Failed to get revisions"
      error:(err) ->
        #console.log "Fucking Hell #{err}"
    })
    #console.log "Done Saving"


  saveFailedHandler:(err) ->
    d = $ '<div>'
    $(d).text err.responseText
    $(d).attr { 'class':'alert alert-danger','role':'alert','id':'res-fail' }
    $("#result").html ""
    $("#result").append d
    $("#saveButton").hide()
    $("#btnFailure").show()
    $("#save").attr {'class': 'fa fa-times-circle btnFailure'}

  save: () ->
    self = this
    postData = this.getContentData()
    data = postData.data
    newPost = $("#newPost").val()
    if newPost == "true"
      result = Q.when jsRoutes.controllers.Authorised.submitPost().ajax postData
      result.then (data)->
        self.saveSucessfulHandler(data)


        self.getRevisions(postData._id.$oid)
      ,(err) ->
        this.saveFailedHandler (data)
    else
      result = Q.when jsRoutes.controllers.Authorised.submitBlogUpdate().ajax postData
      next = result.then this.saveSucessfulHandler , this.saveFailedHandler
      next.then (data) ->
        self.getRevisions(postData._id.$oid)



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
    $("#editor").on 'click', () ->
      $("pre code").each (i, blk) ->
        $(blk).html self.stripSpan $(blk).html()
        hljs.highlightBlock blk

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
         $(target).attr('style', 'margin: 0 0 0 40px border: none padding: 0px')

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
    #console.log target
    while f = files[i]
      imageReader = new FileReader
      imageReader.onload = ((aFile) ->
        (e) ->
          formData = new FormData()
          formData.append("file",aFile)
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
            data: formData
            })
          result.then (data) ->
            $("#result").html(data)
            $("#" + imageId).attr('src', "/" + data)
          , (error) ->
            $("#result").html(error)
      )(f)
      imageReader.readAsDataURL(f)
      i++

# chang this to manipulate the br to \ns
  stripSpan:(html)->
    #console.log "HTML = #{html}"
    elem = $(html)
    #console.log "Elem = #{elem}"
    # make this br and the use
    elem.find("br").each () ->
      $(this).replaceWith("\n")
    "#{elem.text()}"

  codeBlock: ()->
    self = this
    #console.log "hello"
    id = "leek1"
    sel = window.getSelection()
    if $(sel.anchorNode).parents("pre code").length == 0
      text = "//insert your code here"
      if sel.toString() != ""
        text = sel.toString()
        this.pasteHtmlAtCaret("<pre id=\"#{id}\"><code>#{text}</code></pre>")
      else
        this.pasteHtmlAtCaret("<pre id=\"#{id}\"><code>#{text}</code></pre><br/>")
      code = $("##{id}")
      $(code).each (i, block) ->
        hljs.highlightBlock block

      $(code).on 'click', (event) ->
        event.stopPropagation();
    else
      #console.log "has parents"
      node = $(sel.anchorNode).parents("pre code")[0]
      #console.log node.parentNode
      html = self.stripSpan($(node.parentNode).html())
      $(node.parentNode).replaceWith "<br/>" + html.replace(/\n/g,"<br/>")

  loadTags:(id)->
    promise = Q.when jsRoutes.controllers.JsonApi.getContentTags(id).ajax({})
    promise.then (data) ->
      #console.log "leek #{JSON.stringify(data)}"
      $.each data, (key,val) ->
        #console.log "Loop #{val}"
        text = $("#tagBox").val()
        text = text + val
        text = text + ", "

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
        undo:{
          title: "undo",
          image: '<span class="fa fa-undo"></span>',
          click: ()->
            document.execCommand "undo"
        },
        redo:{
          title: "redo",
          image: '<span class="fa fa-repeat"></span>',
          click: ()->
            document.execCommand "redo"
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
          title: "link",
          image: '<span class="fa fa-link"></span >',
          showselection: true,
          showstatic: true
        },
        code: {
          title: "code",
          image: '<span class="fa fa-code"></span >',
          click: () ->
            self.codeBlock()
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
      submit: {
        title: 'Submit',
        image: '\uf00c'
      }
    }
    this.addEditorMenu()

  addImageDropZone: (target) ->
    thumb = $ "<div>"
    thumb.attr {'id':'thumbnail', 'contenteditable':true , 'class':'pull-left albumImage' }
    img = $ "<img>"
    img.attr {'id': target ,'alt':"Drop Biography Image here" , 'src':"" , 'class':"albumImage"}
    thumb.append img
    thumb.insertBefore "#editor"
    utils.createImageDropzone "##{target}", (imgdata) ->
      formData = new FormData()
      formData.append("file",imgdata)
      utils.uploadImage formData, (data) ->
        $("##{target}").attr 'src', "/"+ data
        text = $("#extraDataValues").val()
        if text.match /thumb=/
          alert "leek"
        text = text.replace /thumb=.*/ , "thumb=#{data}"
        $("#extraDataValues").val text
}
