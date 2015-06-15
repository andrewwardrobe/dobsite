define ['common','puff'],(common,puff) -> {
        test:(word)->
            leek =  jsRoutes.controllers.Application
            $("#leek").text "leek "+word
            return "leek " +word
        jsRoutesTest:()->
            leek = jsRoutes.controllers.Application.leek();
            $("#leek").text leek
        nockTest:()->
            console.log("in nock test 2: "+sitebase)
            $.get(sitebase + "/leek", (data) ->
                console.log("in nock test")
                console.log("leek "+data.leek)
                console.log $("#leek").text()
                $("#leek").text data.leek
            ).fail (err) ->
                console.log(err)
    }

