require ["common"], (common) ->
    require ["profile",'jquery'], (profile,$) ->
        profile.setupEditHandler "#editBtn"
        profile.loadPosts()
        profile.loadDrafts()
        profile.addAliasHandler()
