define ['common','jquery'],(common,$) ->
  self = {
    listToTable: (list, target, threshold, cols) ->
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
      count = 0
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
        $(cell).text (idx + 1) + ". " + cellData
        return
      $("#" + list).remove();
      return

    loadImage: (files, target, onSuccess) ->
      self = this
      i = 0
      f = undefined
      while f = files[i]
        imageReader = new FileReader
        imageReader.onload = ((aFile) ->
          (e) ->
            onSuccess(aFile))(f)
        imageReader.readAsDataURL(f)
        i++

    #Todo extract image code to it's own module
    uploadImage: (imageData, onSuccess) ->
      $.ajax
        url: "/upload",
        type: 'POST',
        processData: false,
        contentType: false,
        data: imageData,
        success: (data) ->
          onSuccess data


    createImageDropzone: (selector,onSuccess) ->
      self = this
      $(selector).on 'drop', (e) ->
        self.dropEventHandler e , onSuccess
      $(selector).on 'dragenter', () ->
        false
      $(selector).on 'dragover', () ->
        false

    removeImageDropzone: (selector) ->
      self = this
      $(selector).off 'drop', self.dropEventHandler
      $(selector).off 'dragenter', () ->
        false
      $(selector).off 'dragover', () ->
        false

    dropEventHandler: (e, onSuccess) ->
      e.dataTransfer = e.originalEvent.dataTransfer
      event = e
      dropTarget = event.target or event.srcElement
      console.log dropTarget.id
      if dropTarget.id == ""
        id = dropTarget.parentElement.id
      else
        id = dropTarget.id

      console.log "this " + JSON.stringify self
      self.loadImage event.dataTransfer.files, id, (data) ->
        $("##{id}").attr 'src', data
        onSuccess data


      event.stopPropagation()
      event.preventDefault()
    }
