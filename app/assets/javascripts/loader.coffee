define ["common", 'q'], (common, Q) -> {
  fileData:null

  setupDiscoFileHandler:() ->
    self = this
    $("#discFile").on 'change', (event) ->
      for f in event.target.files
        fileLoader = new FileReader

        fileLoader.onload = ((aFile) ->
          (e) ->
            self.fileData = e.target.result
            console.log self.fileData
        )(f)
        fileLoader.readAsText(f)
        console.log self.fileData

  upload:()->
    result = $("#result")
    if this.fileData != null
      try
        jsdata = JSON.parse this.fileData
        json = {
          data:JSON.stringify(jsdata),
          contentType: 'application/json; charset=utf-8',
          dataType:"json"
        }
        console.log (JSON.stringify(json,null,2))
        promise = Q.when jsRoutes.controllers.AdminJsonApi.insertDiscographies().ajax(json)
        promise.then (data) ->
          $("#result").text "Upload Done. #{data.responseText}"
        , (err) ->
          if err.status == 200
            $("#result").text "Upload Done. #{err.responseText}"
          else
            $("#result").text "Upload Failed: #{err.responseText}"
      catch e
        console.log(e)
        $(result).text "Invalid Data"
    else
      console.log "No Json Data"
      $(result).text "No Data"

  uploadButtonHandles:()->
    self = this
    $("#upload").on 'click', () ->
      self.upload()

  setup:() ->
    this.setupDiscoFileHandler()
    this.uploadButtonHandles()
}
