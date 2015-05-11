define ['common','jquery'],{
        leek:() ->
            $.get "/json/content/bytype/" + '2', (data) ->
                newRow = $("<tr>")
                counter = 0
                cellsPerRow = 4
                leek = $("#albumThumbsTable tr:last") #use this to determine if we need a new row
                if leek.length == 0
                    console.log("No Row Present")
                $("#albumThumbsTable").append newRow
                $.each data, (index, disc) ->
                    newCell = $("<td>")
                    newCell.attr 'id', "disc" + disc.id
                    newRow.append newCell
                    extraData = $.parseJSON(disc.extraData)
                    image = $("<img>")
                    image.attr 'src', extraData.thumb
                    image.attr 'class','discoThumb'
                    image.attr 'data-toggle','modal'
                    image.attr 'data-target', '#RelIDmodal' + disc.id
                    newCell.append image
                    newCell.append $("<br>")
                    newCell.append disc.title
                    newCell.attr 'class', 'discoCell'
                    counter++
                    if counter == cellsPerRow
                        newRow = $("<tr>")
                        $("#albumThumbsTable").append newRow
                        counter = 0
}