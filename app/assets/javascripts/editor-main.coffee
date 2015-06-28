require ['common'], (common) ->
    require ['editor','jquery'], (editor,$) ->
        editor.setupEditor()
        postId = $("#postId").attr 'value'
        console.log "Post ID = " + postId
        editor.loadContentPost postId, "HEAD"
        editor.getRevisions postId
        editor.applyToolbarHandlers()
