require(["common"], function(common) {
   console.log('Dashboard started');

   require(['leek','dob'],function(leek,dob){
    leek.hello("Andrew");
    dob.leek();
   });
});