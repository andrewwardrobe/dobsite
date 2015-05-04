@populatePage = (postId) ->
 json = {
    success: (data) ->
        console.log JSON.stringify(data)
        $("#postTitle").text data.title
        $("#editor").html data.content
        $("#author").text data.author
        d = new Date(data.dateCreated)
        dateStr = d.toLocaleDateString()
        $("#dateCreated").text dateStr
        $("#postId").val data.id
        if data.extraData != ""
            console.log(data.extraData)
            extraData = $.parseJSON(data.extraData)
            if extraData.thumb != ""
                img = $("<img>")
                img.attr 'id','bioImage'
                img.attr 'src', extraData.thumb
                img.attr 'class', 'pull-left bioImage img-responsive'
                $("#editor").prepend img

    error: (data) ->
        $("#postTitle").text "No such item"
        $("#editor").text "Item Requested Could not be found"
 }
 jsRoutes.controllers.JsonApi.getPostById(postId).ajax(json)


doPage = () ->
    postId = $("#postId").val()
    populatePage(postId)

doPage()