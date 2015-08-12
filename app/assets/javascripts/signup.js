
function checkPasswordStrength(){
    var passwd = $('#password').val();
    var maxScore = 5;
    var score =0;
    if(passwd.length >= 8){ score++; }
    if(passwd.match(/[A-Z]/)){ score++; }
    if(passwd.match(/[0-9]/)){ score++; }
    if(passwd.match(new RegExp('([!,%,&,@,#,$,^,*,?,_,~])'))){ score++; }

    if(passwd.match(/[Pp][_]*assword[0-9]*/)){
        $("#passErrs").text("Shit password mate");
        score=0;
    }

    if(passwd.match($("#name").val())){
        $("#passErrs").text("Password cannot match screen name");
        score=0;
    }

    if(passwd.match($("#email").val())){
        $("#passErrs").text("Password cannot match");
        score=0;
    }

    if(score <= 1){
        $("#passInfo").text("Password: Very Weak");
        $('#password').attr('class','form-control dob-pass-vweak');
    }else if(score == 2){
        $("#passInfo").text("Password: Weak");
        $('#password').attr('class','form-control dob-pass-weak');
    }else if(score == 3){
        $("#passInfo").text("Password: Ok");
        $('#password').attr('class','form-control dob-pass-ok');
    }else if(score >= 4){
        $("#passInfo").text("Password: Good");
        $('#password').attr('class','form-control dob-pass-good');
    }

    return score;
}

function checkPassword(){
    var passwd = $('#password').val();
    var conf = $('#confirm').val();
    if(passwd.length >= 6){
        if(passwd==conf){
            $("#passErrs").text("");
            $('#confirm').attr('class','form-control dob-control-ok');
            return checkPasswordStrength() ? true : false;
        }else{
            $("#passErrs").text("Passwords do not match");
            $('#confirm').attr('class','form-control dob-control-bad');
            return false;
        }
    }else{
        $("#passErrs").text("Password too short");
        return false;
    }
}

$("#confirm").on('keyup',function(){
    var passwd = $('#password').val();
    var conf = $('#confirm').val();
    if(passwd==conf){
        $("#passErrs").text("");
        $('#confirm').attr('class','form-control dob-control-ok');
    }else{
        $("#passErrs").text("Passwords do not match");
        $('#confirm').attr('class','form-control dob-control-bad');

    }
});

$("#password").on('keyup',function(){
  var passwd = $('#password').val();
  var conf = $('#confirm').val();
  if(passwd.length >= 6){
    $("#passErrs").text("");
    checkPasswordStrength();
  }else{
      $("#passErrs").text("Password too short");
      $('#password').attr('class','form-control dob-control-bad');
  }
});

function checkName(){
    var name = $("#name").val();
    if(name.length < 6){
       $("#nameErrs").text("Screen name too short");
       $("#name").attr('class','form-control dob-control-bad');
        return;
    }
    if(name.match(/^@/)){
       $("#nameErrs").text("This isn't twitter mate");
       $("#name").attr('class','form-control dob-control-bad');
       return;
    }

     if(name.match(/^[A-Za-z0-9_ ]+[A-Za-z0-9_]$/)){
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
        $("#nameErrs").text("Screen name can only contain letters, numbers or underscores");
        $("#name").attr('class','form-control dob-control-bad');
    }

}

function checkEmail(){
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

}
$("#name").on('keyup',function(){checkName();});

$("#email").on('keyup',function(){checkEmail();});

function doNameValidation(){
    var errs = 0;
    var name = $("#name").val();

    if(name.length < 6){
        errs++;
    }
    //Probably not the best way to do this
    if($('#name').attr('class') != 'form-control dob-control-ok'){
        errs++;
    }

    return errs === 0 ? true : false ;
}

function doEmailValidation(){
    var errs = 0;
    var name = $("#email").val();

    if(name.length < 6){
        errs++;
    }

    //check the user name is avaible
    //jsRoutes.controllers.UserServices.checkEmail(email).ajax(json);
    //Probably not the best way to do this
    if($('#email').attr('class') != 'form-control dob-control-ok'){
        errs++;
    }

    return errs === 0 ? true : false ;
}

$('#signupForm').submit(function(){
    var result;
    if(checkPassword() && doNameValidation() && doEmailValidation){
        result = true;
    }else{
        alert("Validation Failed");
        result = false;
    }

    return result;
});

