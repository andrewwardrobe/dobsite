require ['common'], (common) ->
    require ['editor','jquery'], (editor,$) ->
        editor.setupEditor()
        editor.setupEditorBox()
        postId = $("#postId").attr 'value'
        editor.loadContentPost postId, "HEAD"
        editor.loadTags postId
        editor.getRevisions postId

        editor.applyToolbarHandlers()
        editor.warningBoxHandlers()
