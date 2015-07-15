require ['common'], (common) ->
    require ['news','jquery'], (news,$) ->
        news.setupClickHandler(1)
