$("#userName").on('keyup',function(e){
       var code = (e.keyCode || e.which);

       // do nothing if it's an arrow key
       if(code == 37 || code == 38 || code == 39 || code == 40) {
           return;
       }

       var json = {
           success: function(data){
               var dataList = $("#userNameList");
               $(dataList).html("");
               for(var i = 0; i < data.length ; i++){
                   var opt = $("<option>");
                   $(opt).html(data[i]);
                   $(dataList).append(opt);
               }
           }
       };
       var name = $("#userName").val();
       jsRoutes.controllers.AdminJsonApi.getUsers(name).ajax(json);
   });

$("#btnLoad").on("click",function(e){
    var name = $("#userName").val();
    var json = {
        success: function (data){
            $("#email").val(data.email);
            $("#role").val(data.role);
            $("#loadErrs").text("User "+name+" Loaded");
            $("#loadErrs").attr('class','alert alert-success skinnyAlert');
        },
        error:function (data){
          $("#loadErrs").text("Failed to load user "+name);
          $("#loadErrs").attr('class','alert alert-danger skinnyAlert');
        }
    };
    jsRoutes.controllers.AdminJsonApi.getUser(name).ajax(json);
});

$("#btnRole").on('click',function(){
    var name = $("#userName").val();
    var json = {
        success: function (data){
            console.log("Success");
            $("#roleErrs").text("Role updated");
            $("#roleErrs").attr('class','alert alert-success skinnyAlert');
        },
        error: function (data){
            console.log("Fail ");
            for (var i = 0 ; i < data.length ; i ++){
            console.log(data[i]);
            }
        }
    };
    var role = $("#role").val();
    jsRoutes.controllers.Admin.changeRole(name,role).ajax(json);
});

$("#btnEmail").on('click',function(){
    var name = $("#userName").val();
    var json = {
        success: function (data){
            console.log("Success");
            $("#emailErrs").text("Email updated");
            $("#emailErrs").attr('class','alert alert-success skinnyAlert');
        },
        error: function (data){
            console.log("Fail ");
            for (var i = 0 ; i < data.length ; i ++){
            console.log(data[i]);
            }
        }
    };
    var email = $("#email").val();
    jsRoutes.controllers.Admin.changeEmail(name,email).ajax(json);
});

$("#btnPass").on('click',function(){
    var name = $("#userName").val();
    var pass = $("#password").val();
    var confirm = $("#confirm").val();
    var json = {
        data:{
            "userName": name,
            "password": pass,
            "confirm": confirm
        },
        success: function (data){
            console.log("Success");
            $("#passErrs").text("Password updated");
            $("#passErrs").attr('class','alert alert-success skinnyAlert');
        },
        error: function (data){
            console.log("Failed " +data.error);
        }
    };
    var role = $("#role").val();

    if(pass == confirm){
        jsRoutes.controllers.Admin.changePassword().ajax(json);
    }else{
        $("#passErrs").text("Passwords do not match");
        $("#passErrs").attr('class','alert alert-danger height: 24px;');
    }
});