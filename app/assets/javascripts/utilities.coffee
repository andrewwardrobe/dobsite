define ['common','jquery'],(common,$) ->
    {
        listToTable:(list, target, threshold, cols) ->
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
    }