define ['common','jquery','utilities'],(common,$,utils) -> {
    setupEditHandler:(selector) ->
        console.log "Setting up click handle for #{selector}"
        $(selector).on 'click', () ->
            console.log "Clicked"
            $("#about").attr 'contenteditable', 'true'
}
