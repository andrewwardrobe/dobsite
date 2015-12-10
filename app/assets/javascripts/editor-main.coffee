require ['common'], (common) ->
    require ['editor','jquery'], (editor,$) ->
        editor.setupEditor()
        editor.setupEditorBox()
        postId = $("#postId").val()
        editor.addImageDropZone("bioImage")
        editor.loadContentPost postId, "HEAD"
        #editor.loadTags postId
        editor.getRevisions postId

        editor.applyToolbarHandlers()
        editor.warningBoxHandlers()


