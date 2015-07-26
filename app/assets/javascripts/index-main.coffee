require ["common",'jquery.mCustomScrollbar'], (common) ->
    require ["index",'jquery'], (index ,$) ->
        index.loadLatestPosts();