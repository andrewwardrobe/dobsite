define ['common','jquery','utilities'],(common,$,utils) ->
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

        editModeOn:(selector) ->
            self = this
            $(selector).attr 'class','editBtnOn'
            $("#about").attr 'contenteditable', 'true'
            $("#avatarDiv").attr 'contenteditable', 'true'
            self.createImageDropzone("#avatarDiv");
            self.editMode = true

        editModeOff:(selector) ->
            self = this
            $(selector).attr 'class','editBtnOff'
            $("#about").attr 'contenteditable', 'false'
            $("#avatarDiv").attr 'contenteditable', 'false'
            self.removeImageDropzone("#avatarDiv");
            self.editMode = false

        loadImage:(files, target,onSuccess) ->
            self = this
            count = 1
            i = 0
            f = undefined
            while f = files[i]
              imageReader = new FileReader

              imageReader.onload = ((aFile) ->
                (e) ->
                    $("#"+target).attr 'src', e.target.result
                    avatarImage = aFile
                    self.uploadImage aFile, onSuccess

              )(f)
              imageReader.readAsDataURL(f)
              i++

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
            event = e.originalEvent
            target = event.target or event.srcElement
            console.log target.id
            if target.id == ""
                id =  target.parentElement.id
            else
                id =  target.id

            profile.loadImage event.dataTransfer.files, id, (data) ->
                $("##{id}").attr 'src', data

            event.stopPropagation()
            event.preventDefault()


    }
    profile
