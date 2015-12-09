require ['common'], (common) ->
    require ['editor','jquery'], (editor,$) ->
        editor.setupEditor()
        editor.setupEditorBox()
        postId = $("#postId").val()
        editor.loadContentPost postId, "HEAD"
        #editor.loadTags postId
        editor.getRevisions postId

        editor.applyToolbarHandlers()
        editor.warningBoxHandlers()
        editor.addImageDropZone("#bioImage")

