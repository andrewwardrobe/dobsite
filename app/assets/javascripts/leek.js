define(['common','jquery'],{
        hello:function(name){
            console.log("hello "+name);
            $("#jimmy").text("Hello "+name);
        }
});