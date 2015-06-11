define ['common','jquery','puff'],(common,$,puff) -> {
        test:(word)->
            leek =  jsRoutes.controllers.Application
            $("#leek").text "leek "+word
            return "leek " +word
        jsRoutesTest:()->
            leek = jsRoutes.controllers.Application.leek();
            $("#leek").text leek
        getTest:()->
            console.log "In getTest()"
            $.get "/dob/leek", (data) ->
                console.log JSON.stringify data
                $.each data, (idx, val) ->
                    console.log JSON.stringify val
                    $("#leek").text val.leek
    }

