define ['common','jquery','puff'],(common,$,puff) -> {
        test:(word)->
            leek =  jsRoutes.controllers.Application
            $("#leek").text "leek "+word
            return "leek " +word
    }

