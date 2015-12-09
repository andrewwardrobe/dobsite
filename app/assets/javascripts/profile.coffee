define ['common','utilities','q'],(common,utils,Q) ->
    profile = {
        avatarImage : undefined
        editMode:false

        setupEditHandler:(selector) ->
            self = this
            console.log "Setting up click handle for #{selector}"
            $(selector).on 'click', () ->
                console.log "Clicked"
                if self.editMode == false
                    self.editModeOn selector
                else
                    self.editModeOff selector

        addAliasHandler: ()->
            $("#addAlias").on 'click', ()->
                console.log "click"
                alias = $("#aliasInput").val()
                console.log alias
                if alias != ""
                    result = Q.when jsRoutes.controllers.Authorised.addAlias(alias).ajax({})
                    result.then (data) ->
                        console.log "Aliias Added"
                        $("#aliasList").append("#{data},")
                    , (err) ->
                        console.log(JSON.stringify(err))
                        $("#errs").text(err.responseText)


        editModeOn:(selector) ->
            self = this
            $(selector).attr 'class','editBtnOn'
            $("#about").attr 'contenteditable', 'true'
            $("#about").on 'keyup', ()->
                $("#saveBtn").show()
            $("#avatarDiv").attr 'contenteditable', 'true'
            self.createImageDropzone("#avatarDiv");
            self.editMode = true
            $('#saveBtn').on 'click', self.save

        editModeOff:(selector) ->
            self = this
            $(selector).attr 'class','editBtnOff'
            $("#about").attr 'contenteditable', 'false'
            $("#avatarDiv").attr 'contenteditable', 'false'
            self.removeImageDropzone("#avatarDiv");
            self.editMode = false
            $('#saveBtn').off 'click', self.save

        loadImage:(files, target,onSuccess) ->
            self = this
            count = 1
            i = 0
            f = undefined
            while f = files[i]
              imageReader = new FileReader
              imageReader.onload = ((aFile) ->
                (e) ->
                    formData = new FormData()
                    formData.append("file",aFile)
                    $("#"+target).attr 'src', e.target.result
                    avatarImage = aFile
                    self.uploadImage formData, onSuccess

              )(f)
              imageReader.readAsDataURL(f)
              i++
        #Todo extract image code to it's own module
        uploadImage:(imageData,onSuccess) ->
            $.ajax
                url: "/upload",
                type: 'POST',
                processData: false,
                contentType: false,
                data: imageData,
                success: (data) ->
                    onSuccess data


        createImageDropzone : (selector) ->
            self = this
            $(selector).on 'drop', self.dropEventHandler
            $(selector).on 'dragenter',() ->
                false
            $(selector).on 'dragover',() ->
                false

        removeImageDropzone : (selector) ->
            self = this
            $(selector).off 'drop', self.dropEventHandler
            $(selector).off 'dragenter',() ->
                false
            $(selector).off 'dragover',() ->
                false

        dropEventHandler: (e) ->
            self = this
            e.dataTransfer = e.originalEvent.dataTransfer
            event = e
            dropTarget = event.target or event.srcElement
            console.log dropTarget.id
            if dropTarget.id == ""
                id =  dropTarget.parentElement.id
            else
                id =  dropTarget.id

            self.loadImage event.dataTransfer.files, id, (data) ->
                $("##{id}").attr 'src', data
                $("#saveBtn").show()

            event.stopPropagation()
            event.preventDefault()


        save: ()->
            profId = $("#profileId").val()
            user = $("#userId").val()
            about = $("#about").text()
            avtr = $("#avatar").attr 'src'
            json = {
                data:
                    "id":profId,
                    "userId":user,
                    "about":about,
                    "avatar":avtr

                success:()->
                    $("#saveBtn").hide()
                    $("#saveSuccess").show()

                error:()->
                    $("#saveFailed").show()

            }
            console.log JSON.stringify json
            jsRoutes.controllers.Authorised.updateProfile().ajax(json)

        loadPosts:()->
            userId = $("#userId").val()
            json = {
                success:(data)->
                    $.each data, (idx,post) ->
                        postDiv = $("<div>")
                        newLink = $("<a>")
                        newLink.attr 'id','postLink'+post._id.$oid
                        newLink.attr 'href',post._id.$oid
                        newLink.text post.title
                        editLink = $("<a>")
                        editLink.text "(edit)"
                        editLink.attr 'id','editLink'+post._id.$oid
                        editLink.attr 'href',"editor/"+post._id.$oid
                        postDiv.append newLink
                        postDiv.append " "
                        postDiv.append editLink

                        $("#myPosts").append postDiv
            }
            jsRoutes.controllers.JsonApi.getContentByUserLatestFirst(userId).ajax(json)
        loadDrafts:()->
            userId = $("#userId").val()
            json = {
                success:(data)->
                    $.each data, (idx,post) ->
                        postDiv = $("<div>")
                        newLink = $("<a>")
                        newLink.attr 'id','draftLink'+post._id.$oid
                        newLink.attr 'href',"editor/"+post._id.$oid
                        newLink.text post.title
                        postDiv.append newLink
                        $("#myDrafts").append postDiv
            }
            jsRoutes.controllers.JsonApi.getDraftsByUserLatestFirst(userId).ajax(json)

    }
    profile
