define ['common','jquery','puff'],(common,$,puff) -> {
        test:(word)->
            $("#leek").text "leek "+word
            return "leek " +word
    }

