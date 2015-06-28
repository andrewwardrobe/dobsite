require ['common'], (common) ->
    require ['editor','jquery'], (editor,$) ->
        editor.setupEditor()
        editor.loadContentPost $("#postId").val(), "HEAD"
