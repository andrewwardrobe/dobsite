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

@populateTags = (postId) ->
    json = {
        success: (data) ->
            $.each data, (key,val) ->
                link = $("<a>")
                link.attr 'href', '#'
                link.attr 'id', 'tag_'+val
                link.text val
                $("#tags").append link
    }
    jsRoutes.controllers.JsonApi.getContentTags(postId).ajax(json)


doPage = () ->
    postId = $("#postId").val()
    populatePage(postId)
    populateTags(postId)

doPage()