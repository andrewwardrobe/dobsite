doBioPages = (typ,target) ->
     $.get "/json/biography/bytype/" +typ, (data) ->
        newRow = $("<tr>")
        counter = 0
        cellsPerRow = 4
        $(target).append newRow
        $.each data, (index, bio) ->
            newCell = $("<td>")
            newCell.attr 'id', 'bioCell' + bio.id
            newRow.append newCell
            image = $("<img>")
            image.attr 'src', 'assets/'+ bio.thumbPath
            image.attr 'class','bioThumb'
            image.attr 'id', 'bioImage' + bio.id
            newCell.append image
            newCell.append $("<br>")
            newCell.attr 'class', 'bioCell'
            link = $("<a>")
            link.attr 'href', '/biography/' + bio.id
            link.attr 'id', 'bioLink' + bio.id
            link.text(bio.name)
            newCell.append link
            counter++
            if counter == cellsPerRow
                newRow = $("<tr>")
                $(target).append newRow
                counter = 0


doBioPages(0,'#dobBioTable')
doBioPages(1,'#mcBioTable')