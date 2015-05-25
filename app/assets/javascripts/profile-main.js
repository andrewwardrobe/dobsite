require(["common"], function(common) {
  require(['profile'],function(profile){
    profile.setupEditHandler("#editBtn");
    profile.loadPosts();
   });
});