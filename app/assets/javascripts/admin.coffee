define ['common','q'], (common , Q)-> {
    setupUserSearch:()->
        $("#userName").on 'keyup', (e) ->
            code = (e.keyCode or e.which)
            # do nothing if it's an arrow key
            if code == 37 or code == 38 or code == 39 or code == 40
               return
            name = $("#userName").val()
            result = Q.when jsRoutes.controllers.AdminJsonApi.getUsersLike(name).ajax({})
            result.then ()->
                dataList = $("#userNameList")
                $(dataList).html ""
                for  item  in data
                  opt = $("<option>")
                  $(opt).html item
                  $(dataList).append opt

    setupLoadUserButton:()->
        $("#btnLoad").on "click", (e) ->
            name = $("#userName").val()
            result = Q.when jsRoutes.controllers.AdminJsonApi.getUser(name).ajax {}
            result.then (data)->
                $("#email").val data.email
                $("#role").val data.role
                $("#loadErrs").text "User #{name} Loaded"
                $("#loadErrs").attr 'class','alert alert-success skinnyAlert'
            , (err) ->
                  $("#loadErrs").text "Failed to load user #{name}"
                  $("#loadErrs").attr 'class','alert alert-danger skinnyAlert'

    setupChangeRoleButton:()->
        $("#btnRole").on 'click',() ->
            name = $("#userName").val()
            role = $("#role").val()
            result = Q.when jsRoutes.controllers.Admin.changeRole(name,role).ajax {}
            result.then (data) ->
                console.log "Success"
                $("#roleErrs").text "Role updated"
                $("#roleErrs").attr 'class','alert alert-success skinnyAlert'
               ,(errs) ->
                    console.log "Fail :#{JSON.stringify errs}"
                    for err in errs
                        console.log errs
    setupEmailButton:()->
        $("#btnEmail").on 'click', () ->
            name = $("#userName").val()
            email = $("#email").val()
            result = Q.when jsRoutes.controllers.Admin.changeEmail(name,email).ajax(json);
            result.then (data) ->
               console.log "Success"
               $("#emailErrs").text "Email updated"
               $("#emailErrs").attr 'class','alert alert-success skinnyAlert'
            ,(errs)->
               console.log "Fail :#{errs}"
               for err in errs
                   console.log errs

    setupPasswordChangeButton:() ->
        $("#btnPass").on 'click', () ->
            name = $("#userName").val()
            pass = $("#password").val()
            confirm = $("#confirm").val()
            role = $("#role").val()
            json = {
                data:{
                    "userName": name,
                    "password": pass,
                    "confirm": confirm
                }
            }
            if pass == confirm
                result = Q.when jsRoutes.controllers.Admin.changePassword().ajax json
                ressult.then (data) ->
                    console.log "Success"
                    $("#passErrs").text "Password updated"
                    $("#passErrs").attr 'class','alert alert-success skinnyAlert'
                ,(err) ->
                    console.log "Failed " +err.error
            else
                $("#passErrs").text "Passwords do not match"
                $("#passErrs").attr 'class','alert alert-danger height: 24px;'

    setup:() ->
       this.setupPasswordChangeButton()
       this.setupEmailButton()
       this.setupChangeRoleButton()
       this.setupUserSearch()
       this.setupLoadUserButton()
}