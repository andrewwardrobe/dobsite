function getRevisions(){
    var id = $("#postId").val();
    var json = {
               success: function(data){
                    var count = 1;
                    $("#revisions").html("");
                    $.each(data,function(idx,rev){
                        var revDiv = $("<li>");
                        revDiv.attr('id','revId'+count);
                        var dte = new Date(rev.commitDate.replace("BST",""));
                        var link = $("<a>");
                        var commitId = rev.commitId;
                        var id = $("#postId").val();
                        var linkRef = jsRoutes.controllers.JsonApi.getPostRevisionById(id,commitId).url;
                        link.attr('href','#');
                        link.attr('id','revLink'+count);
                        link.text(dte.toLocaleString());
                        revDiv.append(link);
                        $("#revisions").append(revDiv);
                        $(revDiv).on('click',function(){
                            var id = $("#postId").val();
                            if(id != -1){
                                $("#saveButton").attr('value',"Update");
                                jsRoutes.controllers.JsonApi.getPostRevisionById(id,commitId).ajax({
                                    success: function (data){
                                       $("#editor").html(data.content);
                                       $("#postTitle").text = data.title;
                                       $("#author").attr('value', data.author);

                                       $("#dateCreated").attr('value', dateStr);
                                       $("#postType").val(data.postType);
                                       $("#editAlertRevision").show();

                                       var dt = data.dateCreated.replace("BST","");

                                         var d = new Date(dt);
                                         var dateStr = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
                                    }
                                });
                            }
                         });
                        count++;
                    });
               },
               error: function(data){
                 var revDiv = $("<li>");
                 revDiv.attr('id','revId1');
                 revDiv.text("New File");
                 $("#revisions").append(revDiv);
               }
        };
    jsRoutes.controllers.JsonApi.getRevisionsWithDates(id).ajax(json);
}



function save(){
    var dateStr = $("#dateCreated").val();
    var title = $("#postTitle").text();
    var content = $('#editor').cleanHtml();
    var postType = $("#postType").val();
    var id = $("#postId").val();
    var author = $("#author").val();
    var extraData = $("#extraDataValues").val();
    var isDraft = $("#isDraft").hasClass("isDraftOn");
    var tags = $("#tagBox").val();
    var userId = $("#userId").val();
    var json = {
           data: {
                  "id": id,
                  "dateCreated": dateStr,
                  "title": title,
                  "content": content,
                  "author": author,
                  "postType": postType,
                  "filename": "",
                  "extraData": extraData,
                  "isDraft": isDraft,
                  "tags":tags,
                  "userId":userId
           },
           success: function(data){
                var d = $('<div>');
                $(d).text("Saved");
                $(d).attr('class','alert alert-success');
                $(d).attr('role','alert');
                $(d).attr('id','res-success');
                $("#result").html("");
                //$("#result").append(d);
                $("#postId").val(data.id);
                getRevisions();
                $("#saveButton").hide();
                $("#btnSuccessful").show();
                $("*[id*='editAlert']").hide();
                $("#draft").val(data.isDraft);
                $("#userId").val(data.userId);
                if(data.isDraft !== false){
                    $("#editAlertDraft").show();
                }else{
                    $("#editAlertLive").show();
                }
           },
           error: function(data){
               var d = $('<div>');
               $(d).text("Save Failed"+ data);
               $(d).attr('class','alert alert-danger');
               $(d).attr('role','alert');
               $(d).attr('id','res-fail');
               $("#result").html("");
               $("#result").append(d);
               $("#postId").val(data);
               $("#saveButton").hide();
               $("#btnFailure").show();
           }
    };
    if(id == -1){
        jsRoutes.controllers.Authorised.submitBlog().ajax(json);

    }else{
        jsRoutes.controllers.Authorised.submitBlogUpdate().ajax(json);
    }
}





function addEditorMenu(){
    var edFuncLink = $("<a>");
    $(edFuncLink).attr('id',"editorMenu");
    $(edFuncLink).attr('href','#');
    $(edFuncLink).attr('class','dropdown-toggle');
    $(edFuncLink).attr('data-toggle','dropdown');
    $(edFuncLink).html('Revisions<span class="caret"></span>');
    var listElem = $("<li>");
    $(listElem).append(edFuncLink);
    $(listElem).attr('class','dropdown');

    //Add in the menu
    var edFuncMenu = $("<ul>");
    $(edFuncMenu).attr('class','dropdown-menu');
    $(edFuncMenu).attr('role','menu');

    $(edFuncMenu).attr('id','revisions');


  //  $(edFuncMenu).append(revisions);
    $(listElem).append(edFuncMenu);


    $("#rightSideNavBar").prepend(listElem);
}



function setupEditor(){
    var editor = $('#editor');
    $(editor).on('input',function(){
        window.editorChanged = true;
    });
    $(editor).wysiwyg({activeToolbarClass:"btn-dob-toolbar"});
    $('#revisions').affix({
          offset: {
            top: 350
          }
    });
   getRevisions();
}


function stopDropDownPagination(){
  $('.dropdown-menu input, .dropdown label').click(function(e) {
    e.stopPropagation();
  });
}

function addEditorScrollbar(){
    $("#editor").mCustomScrollbar();
}

function unSavedAlert(){
      $("#saveButton").show();
      $("#btnSuccessful").hide();
      $("#btnFailure").hide();
      $("#editAlertUnsaved").show();
 }

function preventEditorBlockQuote(){
  $("#editor").on("DOMNodeInserted",function(event){
      var target = event.target;

      switch(target.nodeName){
          case "BLOCKQUOTE":
              $(target).attr('style','margin: 0 0 0 40px; border: none; padding: 0px;');
              break;
      }
      //Do this when you have this class in a module
   //unSavedAlert();
  });
}

function setupKeyUpEvents(){


    $("#editor").on("keyup",function(e){

        unSavedAlert();
    });
    $("#postTitle").on('keyup',function(e){
         unSavedAlert();
    });
    $("#tagBox").on('keyup',function(e){
         unSavedAlert();
    });
    $("#extraDataValues").on('keyup',function(e){
         unSavedAlert();
    });
    $("#editor").on('change',function(e){
         unSavedAlert();
    });

}

function setupEditorBox(){
    setupEditor();
    preventEditorBlockQuote();
    preventEditorBlockQuote();
    addEditorScrollbar();
    setupKeyUpEvents();
}

/////Toolbar Stuff
function doDraftButtonCss(isDraft){

    if(isDraft){
            $("#isDraft").removeClass("isDraftOn");
            $("#isDraft").addClass("isDraftOff");
            $("#isDraft").attr('title','This post is Live');
     }else{
            $("#isDraft").removeClass("isDraftOff");
            $("#isDraft").addClass("isDraftOn");
            $("#isDraft").attr('title','This post is a draft');
        }
}

function setupSaveButtonHandler(){
    $("#saveButton").click(function(){
        save();
    });
}




function draftModeToggle(){

    var isDraft = $("#isDraft").hasClass("isDraftOn");
    if(isDraft){ //we are currenty a draft and we are going live
        if($("#draft").val() == "true"){ // at the last save we re a draft

         $("#editAlertLive2Draft").hide();
         $("#editAlertDraft2Live").show();
        }else{

         $("#editAlertLive2Draft").hide();
         $("#editAlertDraft2Live").hide();
        }
    }else{

        if($("#draft").val() == "false"){
             $("#editAlertLive2Draft").show();
             $("#editAlertDraft2Live").hide();
         }else{
             $("#editAlertLive2Draft").hide();
             $("#editAlertDraft2Live").hide();
         }
    }
    doDraftButtonCss(isDraft);

}

////End

function loadContentData(){
    $(function(){
        var id = $("#postId").val();
        if(id != -1){
                jsRoutes.controllers.JsonApi.getPostById(id).ajax({
                success: function (data){
                   $("#editor").html(data.content);
                   $("#postId").val(data.id);
                   $("#postTitle").text(data.title);
                   $("#author").attr('value', data.author);
                   var d = new Date(data.dateCreated);
                   var dateStr = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
                   $("#dateCreated").attr('value', dateStr);
                   $("#postType").val(data.postType);
                   $("#userId").val(data.userId);
                   var isDraft;
                  if(data.isDraft !== false){

                   isDraft= false;
                   $("#draft").val(true);
                   $("#editAlertDraft").show();
                  }else{
                   isDraft = true;

                   $("#editAlertLive").show();
                   $("#draft").val(false);

                  }
                  doDraftButtonCss(isDraft);
                   var json = $.parseJSON(data.extraData);
                   var text = "";
                   for (var key in json){
                    if(json.hasOwnProperty(key)){
                        text += key + "=" + json[key] +"\n";
                    }
                   }
                   text.replace("\n$","");
                   $("#extraDataValues").val(text);

                }
            });
        }else{
            $("#editAlertNew").show();
        }
    });
}

function setupAdditionalToolbarHandlers(){
    $("#isDraft").on('click',draftModeToggle);
    $("#expander").on('click',function(e){
        var cls = $("#expanderIcon").attr('class');
        if(cls == "fa fa-compress"){
            $("#toolbar").attr('class','toolbar-compact');
            $("#expanderIcon").attr('class','fa fa-expand');
        }else{
             $("#toolbar").attr('class','toolbar');
             $("#toolbar").attr('style','');
             $("#expanderIcon").attr('class','fa fa-compress');
        }
    });



        $('#toolbar').draggable({stack: "#editor"});
        $("#btnSuccessful").hide();
        $("#btnFailure").hide();

    $('#toolbar').on('dragstart',function(){
        $("#toolbar").attr('class','toolbar-compact');
        $("#tbSpace").hide();
        $("#tbSpace").show();
        $("#expanderIcon").attr('class','fa fa-expand');
    });

    setupSaveButtonHandler();
}

function setupWarningBoxHandlers(){
    $(".alert-close").on('click',function(event){
        $(event.target.parentNode).hide();
    });
}

function hideWarnings(){
    $("*[id*='editAlert']").hide();
}

function loadTags(){
    var postId = $("#postId").val();
    $("#tagBox").val("");
    json = {
        success: function(data){
            console.log("Data" +data);
            $.each(data,function(key,val) {
               var text = $("#tagBox").val();
               text = text + val;
               if(key < data.length -1)
                text = text + ",";

               $("#tagBox").val(text);
            });
            var text = $("#tagBox").val();
            text = text.replace(/(^,)|(,$)/g, "");
             $("#tagBox").val(text);
         },
        error: function(data){
            console.log("Could not fetch tags");
        }
    };
    jsRoutes.controllers.JsonApi.getContentTags(postId).ajax(json);
}

function load(){
    addEditorMenu();
    setupAdditionalToolbarHandlers();
    setupEditorBox();
    setupWarningBoxHandlers();
    hideWarnings();
    setupKeyUpEvents();
    loadContentData();
    loadTags();
}

$(function(){
    load();
});