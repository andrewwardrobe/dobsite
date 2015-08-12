define ['common','utilities'],(common,utils) ->
    {
        makeDiscographyTables:(data) ->
            $.each data, (index, disc) ->
                newCell = $ "<td>"
                newCell.attr 'id', "disc" + disc._id.$oid
                extraData = disc.extraData
                image = $ "<img>"
                image.attr 'src', extraData.thumb
                image.attr 'class','discoThumb'
                image.attr 'data-toggle','modal'
                image.attr 'data-target', '#RelIDmodal' + disc._id.$oid
                newCell.append image
                newCell.append $("<br>")
                link = $ "<a>"
                link.text disc.title
                link.attr 'data-toggle','modal'
                link.attr 'data-target', '#RelIDmodal' + disc._id.$oid
                newCell.append link
                newCell.attr 'class', 'discoCell'
                table
                if extraData.discType == "Album" || extraData.discType == "album"
                    table = "albumThumbsTable"
                else if extraData.discType == "Single" || extraData.discType == "single"
                    table = "singleThumbsTable"
                else if extraData.discType == "EP" || extraData.discType == "ep"
                    table = "epThumbsTable"
                else if extraData.discType == "other"
                    table = "otherThumbsTable"
                else
                    table = "otherThumbsTable"

                targetRow = $("##{table} tr:last")
                if targetRow.length == 0
                    newRow = $("<tr>")
                    $("##{table}").append newRow
                    newRow.append newCell
                else
                    cellsInRow = $(targetRow).children('td').length
                    if cellsInRow < 4
                        targetRow.append newCell
                    else
                        newRow = $("<tr>")
                        $("##{table}").append newRow
                        newRow.append newCell

        loadData:() ->
            self = this
            $.get "/json/content/bytype/" + '2', (data) ->
                self.makeDiscographyTables data
                self.makeDiscographyModals data
                self.doListFormating data

        doListFormating:(data) ->
            $.each data, (index, disc) ->
                utils.listToTable("trackList#{disc._id.$oid}","trackDiv#{disc._id.$oid}",7,2)

        makeDiscographyModals:(data) ->
            $.each data, (index, disc) ->
                extraData = disc.extraData
                outer = $("<div>")
                outer.attr 'class', 'modal fade'
                outer.attr 'id', 'RelIDmodal' + disc._id.$oid
                outer.attr 'tabindex', '-1'
                outer.attr 'role', 'dialog'
                outer.attr 'aria-labelledby', 'relID' + disc._id.$oid + 'Title'
                outer.attr 'aria-hidden', 'true'
                dialog = $("<div>")
                dialog.attr 'class', 'modal-dialog'
                outer.append dialog
                content = $("<div>")
                content.attr 'class', 'modal-content'
                dialog.append content
                header = $("<div>")
                header.attr 'class', 'modal-header'
                header.append title
                content.append header
                btn = $("<button>")
                btn.attr 'class', 'close'
                btn.attr 'aria-hidden', 'true'
                btn.attr 'type', 'button'
                btn.attr 'data-dismiss','modal'
                btn.append "X";
                header.append btn
                title = $("<div>")
                title.attr 'class', 'model-title'
                header.append title
                body = $("<div>")
                body.attr 'class', 'center modal-body'
                content.append body
                newDiv = $("<div>")
                newDiv.attr 'align', 'center'
                img = $("<img>")
                img.attr 'class', 'discoImage img-responsive'
                img.attr 'class', 'discoImage img-responsive'
                img.attr 'src',  extraData.thumb
                newDiv.append img
                body.append newDiv
                $('#modals').append outer
                tracksDiv = $("<div>")
                tracksDiv.attr 'id','trackDiv'+disc._id.$oid
                temp = disc.content.replace /<\/?div>/g, ""
                temp = temp.replace /<ol>/, "<ol id=\"trackList#{disc._id.$oid}\">"
                temp = temp.trim()
                tracks = $.parseHTML temp
                tracksDiv.append tracks
                list = $(tracks).children(':first')
                list.attr 'id', 'trackList' + disc._id.$oid
                content.append tracksDiv
    }