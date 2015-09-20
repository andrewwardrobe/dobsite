define ['common'], (common) -> {
  websocket:undefined

  init:()->
    self = this
    this.websocket = new WebSocket("ws://localhost:9000/qtest")
    this.websocket.onopen = (event) ->
      console.log("Opened Socket")

    this.websocket.onclose = (event) ->
      console.log ("Socket closed")

    this.websocket.onmessage = (event) ->
      console.log "Recieved: " + JSON.stringify event.data
      $("#output").append event.data + "<br/>"

    this.websocket.onerror = (event) ->
      console.error event.data

    $("#send").on 'click', () ->
      jim = $("#textBox").val()
      data = {
        leek: jim
      }
      console.log("Sending Data #{JSON.stringify data}")
      self.send(JSON.stringify data)


  send:(msg) ->
    this.websocket.send(msg)

}