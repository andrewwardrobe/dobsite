define ['common','q'],(common,Q) -> {
        test:(word)->
            leek =  jsRoutes.controllers.Application
            $("#leek").text "leek "+word
            return "leek " +word
        jsRoutesTest:()->
            leek = jsRoutes.controllers.Application.leek();
            $("#leek").text leek
        nockTest:()->
            Q.when $.get sitebase + "/leek"


    }

