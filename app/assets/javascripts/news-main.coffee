require ['common'], (common) ->
    require ['news','jquery'], (news,$) ->
        uri = $("#contentURI").val()
        news.getPosts("today",5,uri)
