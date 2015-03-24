l2t = (list, target, threshold, cols) ->
  `var row`
  `var i`
  `var row`
  items = $('#' + list).children()
  table = $('<table>')
  table.attr 'class', 'table table-condensed table-no-border'
  $('#' + target).append table
  cnt = 0
  numRows = Math.ceil(items.length / cols)
  i = 0
  while i < items.length
    if items.length <= threshold
      row = $('<tr>')
      row.attr 'id', list + '_row' + i
      $(table).append row
    else
      if i % cols == 0
        cnt++
        row = $('<tr>')
        row.attr 'id', list + '_row' + cnt
        $(table).append row
    i++
  count=0
  while count < items.length
    rowNum = if items.length <= threshold then count else count % numRows + 1
    cel = $('<td>')
    rowID = '#' + list + '_row' + rowNum
    rw = $(rowID)
    $(cel).attr 'id', list + '_tab_cell_' + count
    $(rw).append cel
    count++
  $.each items, (idx, value) ->
    `var cell`
    data = $(value).text()
    cell = $('#' + list + '_tab_cell_' + idx)
    text = $(cell).text()
    cellData = data
    $(cell).text (idx+1) + ". " +cellData
    return
  $("#"+list).remove();
  return

doModal = (id, image, title) ->
        outer = $("<div>")
        outer.attr 'class', 'modal fade'
        outer.attr 'id', 'RelIDmodal' + id
        outer.attr 'tabindex', '-1'
        outer.attr 'role', 'dialog'
        outer.attr 'aria-labelledby', 'relID' + id + 'Title'
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
        img.attr 'class', 'discoImage'
        img.attr 'src', 'assets/' + image
        newDiv.append img
        body.append newDiv
        tracks = $("<ol>")
        tracks.attr 'id','trackList'+id
        tracksDiv = $("<div>")
        tracksDiv.attr 'id','trackDiv'+id
        tracksDiv.append tracks
        content.append tracksDiv
        $.get( "/json/tracks/byrelease/" + id, (data) ->
            $.each data, (index, item) ->
               track = $("<li>")
               track.attr 'id', "trackRel" + id
               track.text(item.title)
               tracks.append track
        ).always( (data) ->
            l2t("trackList"+id, "trackDiv"+id, 7, 2);
        )
        $('#modals').append outer


doDiscography = (typ, target)  ->
    $.get "/json/discography/bytype/" +typ, (data) ->
        newRow = $("<tr>")
        counter = 0
        cellsPerRow = 4
        typePrefix = switch
            when typ == 0 then "alb"
            when typ == 1 then "sng"
            when typ == 2 then "ep"
            when typ == 3 then "oth"
            else "RelItem"
        $(target).append newRow
        $.each data, (index, disc) ->
            newCell = $("<td>")
            newCell.attr 'id', typePrefix + disc.id
            newRow.append newCell
            image = $("<img>")
            image.attr 'src', 'assets/'+ disc.imagePath
            image.attr 'class','discoThumb'
            image.attr 'data-toggle','modal'
            image.attr 'data-target', '#RelIDmodal' + disc.id
            newCell.append image
            newCell.append $("<br>")
            newCell.append disc.name
            newCell.attr 'class', 'discoCell'
            doModal(disc.id, disc.imagePath, disc.name)
            counter++
            if counter == cellsPerRow
                newRow = $("<tr>")
                $(target).append newRow
                counter = 0



doDiscography(0,'#albumThumbsTable')
doDiscography(1,'#singleThumbsTable')
doDiscography(2,'#epThumbsTable')
doDiscography(3,'#otherThumbsTable')