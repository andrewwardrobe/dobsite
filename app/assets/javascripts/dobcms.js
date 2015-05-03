function pasteHtmlAtCaret(html, selectPastedContent) {
    var sel, range;
    if (window.getSelection) {
        // IE9 and non-IE
        sel = window.getSelection();
        if (sel.getRangeAt && sel.rangeCount) {
            range = sel.getRangeAt(0);
            range.deleteContents();
            // Range.createContextualFragment() would be useful here but is
            // only relatively recently standardized and is not supported in
            // some browsers (IE9, for one)
            var el =$("div");
            //$(el).html;
            var frag = document.createDocumentFragment(), node, lastNode;
            while ( (node = el.firstChild) ) {
                lastNode = frag.appendChild(node);
            }
            var firstNode = frag.firstChild;
            range.insertNode(frag);
            // Preserve the selection
            if (lastNode) {
                range = range.cloneRange();
                range.setStartAfter(lastNode);
                if (selectPastedContent) {
                    range.setStartBefore(firstNode);
                } else {
                    range.collapse(true);
                }
                sel.removeAllRanges();
                sel.addRange(range);
            }
        }
    } else if ( (sel = document.selection) && sel.type != "Control") {
        // IE < 9
        var originalRange = sel.createRange();
        originalRange.collapse(true);
        sel.createRange().pasteHTML(html);
        if (selectPastedContent) {
            range = sel.createRange();
            range.setEndPoint("StartToStart", originalRange);
            range.select();
        }
    }
}

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
                                       console.log(data.dateCreated);
                                       var dt = data.dateCreated.replace("BST","");
                                         console.log("date is "+dt);
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


$("#editor").on("keyup",function(e){
    $("#saveButton").show();
    $("#btnSuccessful").hide();
    $("#btnFailure").hide();
    $("#editAlertUnsaved").show();
});

$("#saveButton").click(function(){
    var dateStr = $("#dateCreated").val();
    var title = $("#postTitle").text();
    var content = $('#editor').cleanHtml();
    var postType = $("#postType").val();
    var id = $("#postId").val();
    var author = $("#author").val();
    var extraData = $("#extraDataValues").val();
    var isDraft = $("#isDraft").hasClass("isDraftOn");
    console.log("IsDraft: " +isDraft);
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
                  "isDraft": isDraft
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

    console.log("Json: " +JSON.stringify(json));
    if(id == -1){
        jsRoutes.controllers.Authorised.submitBlog().ajax(json);

    }else{
        jsRoutes.controllers.Authorised.submitBlogUpdate().ajax(json);
    }
});






function doCodeFormat()
{
    var coding = 0;
    return function(){
        var listId = window.getSelection().focusNode;
        if(coding === 0){
            if(listId.parentNode.id == "editor"){

                coding = 1;
                $(listId).wrap('<pre><code class="java"></code></pre>');
                $('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
                window.getSelection().focusNode = listId;
            }
        }else{
             if(listId.parentNode.parentNode.parentNode.id == "editor"){
                coding = 0;
                $(listId).unwrap().unwrap();
                window.getSelection().focusNode = listId;
             }
        }
    };
}



function doTabIndent(){
    return function(){
        console.log($(window.getSelection().focusNode).text());
      //  $(window.getSelection().focusNode).insertAtCaret("leeek")
        pasteHtmlAtCaret("&nbsp;&nbsp;");
    };
}

$("#editor").on("DOMNodeInserted",function(event){
    var target = event.target;

    switch(target.nodeName){
        case "BLOCKQUOTE":
            $(target).attr('style','margin: 0 0 0 40px; border: none; padding: 0px;');
            break;
    }
});





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

addEditorMenu();

$(function(){

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


});




$('#toolbar').on('dragstart',function(){
    $("#toolbar").attr('class','toolbar-compact');
    $("#tbSpace").hide();
    $("#tbSpace").show();
    $("#expanderIcon").attr('class','fa fa-expand');
});



  $('.dropdown-menu input, .dropdown label').click(function(e) {
    e.stopPropagation();
  });

(function($){
        $(window).load(function(){
            $("#editor").mCustomScrollbar();
        });
})(jQuery);

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

$(function(){
    $('#toolbar').draggable({stack: "#editor"});
    $("#btnSuccessful").hide();
    $("#btnFailure").hide();
});


function doDraftButtonCss(isDraft){
    console.log("Leek2 "+isDraft);
    console.log("Leek3 "+$("#draft").val());
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

function draftModeToggle(){
    console.log("Toggle clicked");
    var isDraft = $("#isDraft").hasClass("isDraftOn");
    if(isDraft){ //we are currenty a draft and we are going live
        if($("#draft").val() == "true"){ // at the last save we re a draft
        console.log("true true"+isDraft+", "+$("#draft").val());
         $("#editAlertLive2Draft").hide();
         $("#editAlertDraft2Live").show();
        }else{
        console.log("true false"+isDraft+", "+$("#draft").val());
         $("#editAlertLive2Draft").hide();
         $("#editAlertDraft2Live").hide();
        }
    }else{
        console.log("leek"+isDraft+", "+$("#draft").val());
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

$("#isDraft").on('click',draftModeToggle);

$(".alert-close").on('click',function(event){
    $(event.target.parentNode).hide();
});

$("*[id*='editAlert']").hide();

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
               console.log("Extra Data = "+ data);
               console.log("Leek "+data.isDraft);
              var isDraft;
              if(data.isDraft !== false){
              console.log("is draft true");
               isDraft= false;
               $("#draft").val(true);
               $("#editAlertDraft").show();
              }else{
               isDraft = true;
               console.log("is draft false");
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
