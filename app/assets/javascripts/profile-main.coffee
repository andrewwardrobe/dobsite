require ["common"], (common) ->
    require ["profile"], (profile) ->
        profile.setupEditHandler "#editBtn"
        profile.loadPosts()
        profile.loadDrafts()
