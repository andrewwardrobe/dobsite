require(["common"], function(common) {
   console.log('Dashboard started');

   require(['discography'],function(disco){
    disco.leek();
   });
});