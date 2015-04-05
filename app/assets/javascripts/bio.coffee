imageDropped = (files, target) ->
    count = 1
    i = 0
    f = undefined
    console.log target
    while f = files[i]
      imageReader = new FileReader

      imageReader.onload = ((aFile) ->
        (e) ->
            console.log 'leek ' + aFile
            console.log 'filesize '+ aFile.size / 1024
            console.log 'filetype '+ aFile.type
            $("#"+target).attr 'src', e.target.result
            $.ajax
                url: "/upload",
                type: 'POST',
                processData: false,
                contentType: false,
                data: aFile,
                success: (data) ->
                    $("#"+target).attr('src',data);
                    saveBtn = target.replace "Image", "Save"
                    extra = target.replace "Image", "Extra"
                    $("#"+saveBtn).show()
                    extraData = $("#"+extra ).val()
                    extraData = extraData.replace("thumb=.*\n","")
                    extraData += "thumb=" + data + "\n"
                    $("#"+extra ).val extraData
      )(f)
      imageReader.readAsDataURL(f)
      i++

@dropper = (e) ->
    target = e.target or e.srcElement

    if target.id == ""
        id =  target.parentElement.id
    else
        id =  target.id

    imageDropped e.dataTransfer.files, id
    e.stopPropagation()
    e.preventDefault()



@expandDiv = (id) ->
    div = $("#bioText"+id)
    clss =  $(div).attr 'class'
    if clss == "bioText"
        $(div).attr 'class', 'bioTextExpanded'
        $("#expander"+id).html '<span class="fa fa-chevron-circle-up"></span>'
    else
        $(div).attr 'class', 'bioText'
        $("#expander"+id).html '<span class="fa fa-chevron-circle-down"></span>'

#doBioPost = (bsRow,
@editModeOn = (id) ->
    editBtn = $("#bioEdit" +id)
    bioText = $("#bioText" +id)
    nameDiv = $("#bioName" +id)
    imageDiv = $("#bioImageDiv"+ id)
    textDiv = $("#bioDiv" + id)
    bioText.attr 'contenteditable','true'
    nameDiv.attr 'contenteditable','true'
    imageDiv.attr 'contenteditable','true'
    imageDiv.attr 'ondrop', 'dropper(event)'
    imageDiv.attr 'ondragenter', 'return false'
    imageDiv.attr 'ondragover', 'return false'
    editBtn.attr 'class', 'editBtnOn'
    editBtn.attr "onclick", "editModeOff(" + id + ")"

@editModeOff = (id) ->
    editBtn = $("#bioEdit" +id)
    bioText = $("#bioText" +id)
    nameDiv = $("#bioName" +id)
    imageDiv = $("#bioImageDiv"+ id)
    textDiv = $("#bioDiv" + id)
    bioText.attr 'contenteditable','false'
    nameDiv.attr 'contenteditable','false'
    imageDiv.attr 'contenteditable','false'
    imageDiv.attr 'ondrop', ''
    imageDiv.attr 'ondragenter', ''
    imageDiv.attr 'ondragover', ''
    editBtn.attr 'class', 'editBtnOff'
    editBtn.attr "onclick", "editModeOn(" + id + ")"

doEditMode = (bio) ->
    bioText = $("#bioText" + bio.id)
    nameDiv = $("#bioName" + bio.id)
    imageDiv = $("#bioImageDiv" + bio.id)
    textDiv = $("#bioDiv" + bio.id)
    bioText.attr 'contenteditable','false'
    nameDiv.attr 'contenteditable','false'
    imageDiv.attr 'contenteditable','false'
    imageDiv.attr 'ondrop', ''
    imageDiv.attr 'ondragenter', ''
    imageDiv.attr 'ondragover', ''
    btnsDiv = $("<div>")
    btnsDiv.attr 'id','btnsDiv'
    btnsDiv.attr 'class', 'pull-right'
    saveBtn = $("<a>")
    saveBtn.html '<span class="fa fa-save"></span>'
    saveBtn.attr 'id', 'bioSave' + bio.id
    saveBtn.attr 'href', '#'
    saveBtn.attr 'class', 'expander'
    editBtn = $("<a>")
    editBtn.html '<span class="fa fa-pencil"></span>'
    editBtn.attr 'id', 'bioEdit' + bio.id
    editBtn.attr 'href', '#'
    editBtn.attr 'class', 'editBtnOff'
    saveSuccess = $("<a>")
    saveSuccess.attr 'id', 'bioSuccess' + bio.id
    saveSuccess.attr 'href', '#'
    saveSuccess.attr 'class', 'btnSuccessful'
    saveSuccess.html '<span class="fa fa-check-circle"></span>'
    saveSuccess.hide()
    saveFailure = $("<a>")
    saveFailure.attr 'id', 'bioFailure' + bio.id
    saveFailure.attr 'href', '#'
    saveFailure.attr 'class', 'btnFailure'
    saveFailure.html '<span class="fa fa-times-circle"></span>'
    saveFailure.hide()
    editBtn.attr "onclick", "editModeOn(" + bio.id + ")"
    saveBtn.on "click", (event) ->
        extraData = $("#bioExtra" +bio.id).val()
        id = event.target.parentElement.id.replace "bioSave" , ""
        dte = $("#bioDate" +bio.id).val()
        title = $("#bioName" +bio.id).text()
        textId = "#bioText" + bio.id
        content = $(textId).html()
        $("#bioSuccess"+bio.id).hide()
        $("#bioFailure"+bio.id).hide()
        console.log event.target.id
        json = {
            data:
                "id":bio.id,
                "dateCreated":dte,
                "title":title,
                "content":content
                "author":bio.author,
                "postType":bio.postType,
                "filename":"",
                "extraData":extraData,
            success: (data) ->
                $("#bioSuccess"+bio.id).show()
                $("#bioSave"+bio.id).hide()
                console.log("Success")
            error: (data) ->
                $("#bioFailure"+bio.id).show()
                console.log("Error")
        }
        jsRoutes.controllers.Authorised.submitBlogUpdate().ajax(json);
        console.log("Buuton Clicked: " +JSON.stringify(json))
    btnsDiv.append editBtn
    btnsDiv.append saveBtn
    btnsDiv.append saveSuccess
    btnsDiv.append saveFailure
    nameDiv.append btnsDiv
    $("#bioSave"+bio.id).hide()
    textDiv.on "keyup", ->
        $("#bioSave"+bio.id).show()
        $("#bioSuccess"+bio.id).hide()

extraDataJs2KeyVal = (js) ->
    data = $.parseJSON(js)
    dataStr = ""
    $.each data, (key, val) ->
        dataStr += key + "=" + val + "\n"
    dataStr

doTextAndImage = (bio, target) ->
    bsRow2 = $("<div>")
    bsRow2.attr 'class','col-sm-5'
    $(target).append bsRow2
    extraData = $.parseJSON(bio.extraData)
    imageDiv = $("<div>")
    image = $("<img>")
    image.attr 'src', extraData.thumb
    image.attr 'class','bioThumb pull-left'
    image.attr 'id', 'bioImage' + bio.id
    imageDiv.attr 'id', 'bioImageDiv' + bio.id
    imageDiv.append image
    textDiv = $("<div>")
    textDiv.attr 'id', 'bioDiv' + bio.id
    textDiv.attr 'class', 'col-xs-12 col-sm-12 bioDiv'
    textDiv.append imageDiv
    nameDiv = $("<div>")
    nameDiv.text bio.title
    nameDiv.attr 'id','bioName' + bio.id
    bioText = $("<div>")
    bioText.attr 'class','bioText'
    bioText.attr 'id','bioText' + bio.id
    bioText.attr 'contenteditable','false'
    bioText.html bio.content
    textDiv.append nameDiv
    #Some hidden stuff
    dateCreated = $("<input>")
    dateCreated.attr 'id', 'bioDate' + bio.id
    dateCreated.attr 'type', 'hidden'
    bioExtra = $("<input>")
    bioExtra.attr 'id', 'bioExtra' + bio.id
    bioExtra.attr 'type', 'hidden'
    bioExtra.attr 'value', extraDataJs2KeyVal bio.extraData
    textDiv.append bioExtra
    d = new Date(bio.dateCreated);
    dateStr = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
    dateCreated.attr 'value', dateStr
    textDiv.append dateCreated
    author = $("<input>")
    author.attr 'id', 'bioAuthor' + bio.id
    author.attr 'type', 'hidden'
    author.attr 'value', bio.author
    textDiv.append author
    iconDiv =  $("<div>")
    iconDiv.attr 'align', 'right'
    textDiv.append iconDiv
    bsRow2.append textDiv
    textDiv.append bioText

doExpander = (bio) ->
    textDiv = $("#bioDiv" + bio.id)
    rightDiv = $("<div>")
    rightDiv.attr 'align','right'
    expander = $("<a>")
    expander.attr 'href','#'
    expander.attr 'id', 'expander' + bio.id
    expander.html '<span class="fa fa-chevron-circle-down"></span>'
    expander.attr 'onclick',"expandDiv( " + bio.id + ")"
    expander.attr 'class','expander'
    rightDiv.append expander
    textDiv.append rightDiv

doBioDivsFromPost = (target) ->
     $.get "/json/content/bytype/" + '4', (data) ->
        bsRow = $("<div>")
        bsRow.attr 'class','row'
        $(target).append bsRow
        editMode = $(target).attr 'edit-mode'
        counter = 0
        $.each data, (index, bio) ->
            doTextAndImage(bio,bsRow)
            doExpander(bio)

            if editMode == "1"
                doEditMode(bio)
            counter++

            if counter == 2
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                counter = 0

emptyDiv = (target) ->
        bsRow = $("<div>")
        bsRow.attr 'class','row'
        $(target).append bsRow
        bsRow2 = $("<div>")
        bsRow2.attr 'class','col-sm-5'
        bsRow.append bsRow2
        image = $("<img>")
        image.attr 'src', 'assets/images/crew/donalds_bw.jpg'
        image.attr 'class','bioThumb pull-left'
        image.attr 'id', 'bioImage' + 0
        textDiv = $("<div>")
        textDiv.attr 'id', 'bioDiv'
        textDiv.attr 'class', 'col-xs-12 col-sm-12 bioDiv'
        textDiv.append image
        nameDiv = $("<div>")
        nameDiv.text "New Biography"
        bioText = $("<div>")
        bioText.attr 'class','bioText'
        bioText.attr 'id','bioText'
        bioText.attr 'contenteditable','true'
        bioText.text "Some example content"
        textDiv.append nameDiv
        bsRow2.append textDiv
        textDiv.append bioText
        rightDiv = $("<div>")
        rightDiv.attr 'align','right'
        expander = $("<a>")
        expander.attr 'href','#'
        expander.attr 'id', 'expander'
        expander.text "Expand"
        expander.attr 'onclick',"expandDiv(" + "" + ")"
        expander.attr 'class','expander'
        rightDiv.append expander
        textDiv.append rightDiv

doBioDivsFromPost('#leekDiv')
