define ['common', 'helpers/date'], (common) -> {
    imageDropped : (files, target) ->
        count = 1
        i = 0
        f = undefined
        console.log target
        while f = files[i]
          imageReader = new FileReader

          imageReader.onload = ((aFile) ->
            (e) ->
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

    dropper : (e) ->
        target = e.target or e.srcElement

        if target.id == ""
            id =  target.parentElement.id
        else
            id =  target.id

        imageDropped e.dataTransfer.files, id
        e.stopPropagation()
        e.preventDefault()



    expandDiv : (id) ->
        div = $("#bioText"+id)
        clss =  $(div).attr 'class'
        if clss == "bioText"
            $(div).attr 'class', 'bioTextExpanded'
            $("#expander"+id).html '<span class="fa fa-chevron-circle-up"></span>'
        else
            $(div).attr 'class', 'bioText'
            $("#expander"+id).html '<span class="fa fa-chevron-circle-down"></span>'

    #doBioPost = (bsRow,
    editModeOn : (id) ->
        self = this
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
        editBtn.on "click", () ->
            self.editModeOff id

    editModeOff : (id) ->
        self = this
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
        editBtn.on "click", () ->
            self.editModeOn id

    doEditMode : (bio) ->
        self = this
        bioText = $("#bioText" + bio._id.$oid)
        nameDiv = $("#bioName" + bio._id.$oid)
        imageDiv = $("#bioImageDiv" + bio._id.$oid)
        textDiv = $("#bioDiv" + bio._id.$oid)
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
        saveBtn.attr 'id', 'bioSave' + bio._id.$oid
        saveBtn.attr 'href', '#'
        saveBtn.attr 'class', 'expander'
        editBtn = $("<a>")
        editBtn.html '<span class="fa fa-pencil"></span>'
        editBtn.attr 'id', 'bioEdit' + bio._id.$oid
        editBtn.attr 'href', '#'
        editBtn.attr 'class', 'editBtnOff'
        saveSuccess = $("<a>")
        saveSuccess.attr 'id', 'bioSuccess' + bio._id.$oid
        saveSuccess.attr 'href', '#'
        saveSuccess.attr 'class', 'btnSuccessful'
        saveSuccess.html '<span class="fa fa-check-circle"></span>'
        saveSuccess.hide()
        saveFailure = $("<a>")
        saveFailure.attr 'id', 'bioFailure' + bio._id.$oid
        saveFailure.attr 'href', '#'
        saveFailure.attr 'class', 'btnFailure'
        saveFailure.html '<span class="fa fa-times-circle"></span>'
        saveFailure.hide()
        editBtn.on "click", () ->
            self.editModeOn bio._id.$oid
        saveBtn.on "click", (event) ->
            extraData = $("#bioExtra" +bio._id.$oid).val()
            id = event.target.parentElement.id.replace "bioSave" , ""
            dte = $("#bioDate" +bio._id.$oid).val()
            title = $("#bioName" +bio._id.$oid).text()
            textId = "#bioText" + bio._id.$oid
            content = $(textId).html()
            userId =  $("#bioUser" +bio._id.$oid).text()
            $("#bioSuccess"+bio._id.$oid).hide()
            $("#bioFailure"+bio._id.$oid).hide()
            console.log event.target.id
            json = {
                data:
                    "_id":bio._id.$oid,
                    "dateCreated":dte,
                    "title":title,
                    "content":content
                    "author":bio.author,
                    "postType":bio.postType,
                    "filename":"",
                    "extraData":extraData,
                    "isDraft":false,
                    "tags":"",
                    "userId":userId
                success: (data) ->
                    $("#bioSuccess"+bio._id.$oid).show()
                    $("#bioSave"+bio._id.$oid).hide()
                    console.log("Success")
                error: (data) ->
                    $("#bioFailure"+bio._id.$oid).show()
                    console.log("Error")
            }
            jsRoutes.controllers.Authorised.submitBlogUpdate().ajax(json);
            console.log("Buuton Clicked: " +JSON.stringify(json))
        btnsDiv.append editBtn
        btnsDiv.append saveBtn
        btnsDiv.append saveSuccess
        btnsDiv.append saveFailure
        nameDiv.append btnsDiv
        $("#bioSave"+bio._id.$oid).hide()
        textDiv.on "keyup", ->
            $("#bioSave"+bio._id.$oid).show()
            $("#bioSuccess"+bio._id.$oid).hide()

    extraDataJs2KeyVal : (js) ->
        data = $.parseJSON(js)
        dataStr = ""
        $.each data, (key, val) ->
            dataStr += key + "=" + val + "\n"
        dataStr

    doTextAndImage : (bio, target) ->
        bsRow2 = $("<div>")
        bsRow2.attr 'class','col-sm-5'
        imageDiv = $("<div>")
        $(target).append bsRow2
        if bio.extraData != ""
            console.log "Extra Data = |#{bio.extraData}|"
            extraData = $.parseJSON(bio.extraData)
            image = $("<img>")
            image.attr 'src', extraData.thumb
            image.attr 'class','bioThumb pull-left'
            image.attr 'id', 'bioImage' + bio._id.$oid
            imageDiv.append image

        imageDiv.attr 'id', 'bioImageDiv' + bio._id.$oid
        textDiv = $("<div>")
        textDiv.attr 'id', 'bioDiv' + bio._id.$oid
        textDiv.attr 'class', 'col-xs-12 col-sm-12 bioDiv'
        textDiv.append imageDiv
        nameDiv = $("<div>")
        nameDiv.text bio.title
        nameDiv.attr 'id','bioName' + bio._id.$oid
        bioText = $("<div>")
        bioText.attr 'class','bioText'
        bioText.attr 'id','bioText' + bio._id.$oid
        bioText.attr 'contenteditable','false'
        bioText.html bio.content
        textDiv.append nameDiv
        #Some hidden stuff
        dateCreated = $("<input>")
        dateCreated.attr 'id', 'bioDate' + bio._id.$oid
        dateCreated.attr 'type', 'hidden'
        bioUser =  $("<input>")
        bioUser.attr 'id', 'bioUser' + bio._id.$oid
        bioUser.attr 'type', 'hidden'
        bioUser.val bio.userId
        bioExtra = $("<input>")
        bioExtra.attr 'id', 'bioExtra' + bio._id.$oid
        bioExtra.attr 'type', 'hidden'
        console.log "Extra Data #{bio.extraData}"
        if bio.extraData != ""
            bioExtra.attr 'value', this.extraDataJs2KeyVal bio.extraData
        textDiv.append bioExtra
        d = new Date(bio.dateCreated)
        dateCreated.attr 'value', d.yyyymmdd()
        textDiv.append dateCreated
        author = $("<input>")
        author.attr 'id', 'bioAuthor' + bio._id.$oid
        author.attr 'type', 'hidden'
        author.attr 'value', bio.author
        textDiv.append author
        iconDiv =  $("<div>")
        iconDiv.attr 'align', 'right'
        textDiv.append iconDiv
        bsRow2.append textDiv
        textDiv.append bioText

    doExpander : (bio) ->
        self = this
        textDiv = $("#bioDiv" + bio._id.$oid)
        rightDiv = $("<div>")
        rightDiv.attr 'align','right'
        expander = $("<a>")
        expander.attr 'href','#'
        expander.attr 'id', 'expander' + bio._id.$oid
        expander.html '<span class="fa fa-chevron-circle-down"></span>'
        expander.on 'click', () ->
            self.expandDiv bio._id.$oid
        expander.attr 'class','expander'
        rightDiv.append expander
        textDiv.append rightDiv

    doBioDivsFromPost : (target) ->
         self = this
         $.get "/json/content/bytype/" + '4', (data) ->
            bsRow = $("<div>")
            bsRow.attr 'class','row'
            $(target).append bsRow
            editMode = $(target).attr 'edit-mode'
            counter = 0
            $.each data, (index, bio) ->
                self.doTextAndImage(bio,bsRow)
                self.doExpander(bio)

                if editMode == "1"
                    self.doEditMode(bio)
                counter++

                if counter == 2
                    bsRow = $("<div>")
                    bsRow.attr 'class','row'
                    $(target).append bsRow
                    bsRow = $("<div>")
                    bsRow.attr 'class','row'
                    $(target).append bsRow
                    counter = 0

    emptyDiv : (target) ->
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
}

