@populatePage = (postId) ->
 json = {
    success: (data) ->
        console.log "Title" + data.title
        $("#postTitle").text data.title
        $("#editor").html data.content
        $("#author").text data.author
        d = new Date(data.dateCreated)
        dateStr = d.toLocaleDateString()
        $("#dateCreated").text dateStr
        $("#postId").val data.id

    error: (data) ->
        $("#postTitle").text "No such item"
        $("#editor").text "Item Requested Could not be found"
 }
 jsRoutes.controllers.JsonApi.getPostById(postId).ajax(json)


doPage = () ->
    postId = $("#postId").val()
    populatePage(postId)

doPage()