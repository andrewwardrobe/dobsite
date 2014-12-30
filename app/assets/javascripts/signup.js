function checkPassword(){
    var passwd = $('#password').val();
    var conf = $('#confirm').val();
    if(passwd.length >= 6){
        if(passwd==conf){
            $("#passErrs").text("");
            $('#confirm').attr('class','form-control dob-control-ok');
            return true;
        }else{
            $$("#passErrs").text("Passwords do not match");
            $('#confirm').attr('class','form-control dob-control-bad');
            return false;
        }
    }else{
        $("#passErrs").text("Password too short");
        return false;
    }
}

$("#password").on('keyup',function(){
  var passwd = $('#password').val();
  var conf = $('#confirm').val();
  if(passwd.length >= 6){
    $('#password').attr('class','form-control dob-control-ok');
  }else{
      $("#passErrs").text("Password too short");
      $('#password').attr('class','form-control dob-control-bad');
  }
});

$("#name").on('keyup',function(){
    var name = $("#name").val();
    if(name.length >= 6){
        var json = {
            success: function(data){
                if(data=="0"){
                    $("#nameErrs").text("");
                    $("#name").attr('class','form-control dob-control-ok');
                }else{
                    $("#nameErrs").text("Screen name in use");
                    $("#name").attr('class','form-control dob-control-bad');
                }
            },
            error: function(data){
                $("#name").attr('class','form-control dob-control-bad');
                console.log("Error occured whilst checking the user name");
            }
        };
       jsRoutes.controllers.UserServices.checkName(name).ajax(json);
    }else{
        $("#nameErrs").text("Screen name too short");
        $("#name").attr('class','form-control dob-control-bad');
    }

});

$("#email").on('keyup',function(){
    var email = $("#email").val();
    var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if(emailRegex.test(email)){
        var json = {
            success: function(data){
                if(data=="0"){
                    $("#emailErrs").text("");
                    $("#email").attr('class','form-control dob-control-ok');
                }else{
                    $("#emailErrs").text("Email address in use");
                    $("#email").attr('class','form-control dob-control-bad');
                }
            },
            error: function(data){
                $("#email").attr('class','form-control dob-control-bad');
                console.log("Error occured whilst checking the email address");
            }
        };
       jsRoutes.controllers.UserServices.checkEmail(email).ajax(json);
    }else{
        $("#emailErrs").text("Enter Valid Email");
        $("#email").attr('class','form-control dob-control-bad');
    }

});


function doNameValidation(){
    var errs = 0;
    var name = $("#name").val();

    if(name.length < 6){
        errs++;
    }
    //Probably not the best way to do this
    if($('#confirm').attr('class') != 'form-control dob-control-ok'){
        errs++;
    }

    return errs === 0 ? true : false ;
}

$('#signupForm').submit(function(){
    var result;
    if(checkPassword() && checkNameValidation()){
        result = true;
    }else{
        alert("Invalid Password");
        result = false;
    }

    return result;
});

