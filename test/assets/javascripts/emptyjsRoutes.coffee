#I don't seem to be able to require js routes without the server being up
#this file is so I can have an empty object structure to stub with sinon
define ['common'],(common) -> {
    controllers:{
        Application:{
            leek:()->
        },
        JsonApi:{
            getPostRevisionById:()->
            getRevisionsWithDates:() -> {
                ajax: ()->
            }
        }
    }
}