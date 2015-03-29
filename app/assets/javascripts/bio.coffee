doBioPages = (typ,target) ->
     $.get "/json/biography/bytype/" +typ, (data) ->
        newRow = $("<tr>")
        counter = 0
        cellsPerRow = 2
        $(target).append newRow
        $.each data, (index, bio) ->
            newCell = $("<td>")
            newCell.attr 'id', 'bioCell' + bio.id
            newRow.append newCell
            bsRow = $("<div>")
            bsRow.attr 'class','row'
            newCell.append bsRow
            image = $("<img>")
            image.attr 'src', 'assets/'+ bio.thumbPath
            image.attr 'class','bioThumb pull-left'
            image.attr 'id', 'bioImage' + bio.id
            bsRow.append image
            #newCell.append $("<br>")
            newCell.attr 'class', 'bioCell'
            link = $("<a>")
            link.attr 'href', '/biography/' + bio.id
            link.attr 'id', 'bioLink' + bio.id
            link.text(bio.name)
            bsRow.append link
            counter++
            if counter == cellsPerRow
                newRow = $("<tr>")
                $(target).append newRow
                counter = 0

doBioDivs = (typ,target) ->
     $.get "/json/biography/bytype/" +typ, (data) ->
        bsRow = $("<div>")
        bsRow.attr 'class','row'
        $(target).append bsRow
        counter = 0
        $.each data, (index, bio) ->
            bsRow2 = $("<div>")
            bsRow2.attr 'class','col-sm-5'
            bsRow.append bsRow2
            image = $("<img>")
            image.attr 'src', 'assets/'+ bio.thumbPath
            image.attr 'class','bioThumb pull-left'
            image.attr 'id', 'bioImage' + bio.id
            textDiv = $("<div>")
            textDiv.attr 'id', 'bioDiv' + bio.id
            textDiv.attr 'class', 'col-xs-12 col-sm-12 bioDiv'
            textDiv.append image
            nameDiv = $("<div>")
            nameDiv.text bio.name
            bioText = $("<div>")
            bioText.attr 'class','bioText'
            bioText.attr 'id','bioText' + bio.id
            bioText.attr 'contenteditable','true'
            bioText.text bio.bioText
            textDiv.append nameDiv
            bsRow2.append textDiv
            textDiv.append bioText
            rightDiv = $("<div>")
            rightDiv.attr 'align','right'
            expander = $("<a>")
            expander.attr 'href','#'
            expander.attr 'id', 'expander' + bio.id
            expander.text "Expand"
            expander.attr 'onclick',"expandDiv( " + bio.id + ")"
            expander.attr 'class','expander'
            rightDiv.append expander
            textDiv.append rightDiv
            counter++
            if counter == 2
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                counter = 0

@expandDiv = (id) ->
    div = $("#bioText"+id)
    clss =  $(div).attr 'class'
    if clss == "bioText"
        $(div).attr 'class', 'bioTextExpanded'
        $("#expander"+id).text "Collaspe"
    else
        $(div).attr 'class', 'bioText'
        $("#expander"+id).text "Expand"

doBioDivsFromPost = (typ,target) ->
     $.get "/json/content/bytype/" + '4', (data) ->
        bsRow = $("<div>")
        bsRow.attr 'class','row'
        $(target).append bsRow
        counter = 0
        $.each data, (index, bio) ->
            bsRow2 = $("<div>")
            bsRow2.attr 'class','col-sm-5'
            bsRow.append bsRow2
            extraData = $.parseJSON(bio.extraData)
            image = $("<img>")
            image.attr 'src', 'assets/'+ extraData.thumb
            image.attr 'class','bioThumb pull-left'
            image.attr 'id', 'bioImage' + bio.id
            textDiv = $("<div>")
            textDiv.attr 'id', 'bioDiv' + bio.id
            textDiv.attr 'class', 'col-xs-12 col-sm-12 bioDiv'
            textDiv.append image
            nameDiv = $("<div>")
            nameDiv.text bio.title
            bioText = $("<div>")
            bioText.attr 'class','bioText'
            bioText.attr 'id','bioText' + bio.id
            bioText.attr 'contenteditable','true'
            bioText.text bio.content
            textDiv.append nameDiv
            bsRow2.append textDiv
            textDiv.append bioText
            rightDiv = $("<div>")
            rightDiv.attr 'align','right'
            expander = $("<a>")
            expander.attr 'href','#'
            expander.attr 'id', 'expander' + bio.id
            expander.text "Expand"
            expander.attr 'onclick',"expandDiv( " + bio.id + ")"
            expander.attr 'class','expander'
            rightDiv.append expander
            textDiv.append rightDiv
            counter++
            if counter == 2
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                bsRow = $("<div>")
                bsRow.attr 'class','row'
                $(target).append bsRow
                counter = 0

doBioDivsFromPost(0,'#leekDiv')
#doBioPages(0,'#dobBioTable')
#doBioPages(1,'#mcBioTable')