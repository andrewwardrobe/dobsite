

function getImageId(){
    var id = 1
    return function(){
        return "postImage" + id++;

    }
}

var imageId = getImageId();


function imagesSelected(myFiles, target) {

    if(target == ""){
        alert('missed dropzone');
        return;
    }

    var count = 1;

  for (var i = 0, f; f = myFiles[i]; i++) {

    var imageReader = new FileReader();
    imageReader.onload = (function(aFile) {
      return function(e) {

        if(aFile.type.lastIndexOf("image/",0) ===0 ){
            var imgName = imageId();
            var divName = "postPara"+imgName
            var div = document.createElement('div');
            div.setAttribute('id',divName);
            var span = document.createElement('img');
            span.setAttribute('id',imgName);
            span.setAttribute('class', 'images');
            span.setAttribute('src',e.target.result);
            span.setAttribute('title',aFile.name);
            div.appendChild(span);

            $.ajax({
                url: "/upload",
                type: 'POST',
                processData: false,
                contentType: false,
                data: aFile
            }).done(function (data) {
                $("#result").html(data);
                $("#"+imgName).attr('src',data);
                }).fail(function(){
                    $("#result").html(data);
            });

            document.getElementById(target).insertBefore(div, null);
            var newEle = document.createElement('p')
            newEle.appendChild(document.createElement('br'))
            document.getElementById(target).appendChild(newEle);
        }else{
            alert("Not an Image")
        }
      };
    })(f);
    imageReader.readAsDataURL(f);
  }
}

function dropIt(e) {
	var target = e.target || e.srcElement;
   imagesSelected(e.dataTransfer.files, target.id);
   e.stopPropagation();
   e.preventDefault();
}

function getNodePosition(){
    var postContent = document.getElementById("postContent")
        var nodes = postContent.childNodes;
        var items = [];
        for(var i in nodes){
            if(nodes[i].nodeType ==1 ){
                items.push(nodes[i])
            }
        }

        items.sort(function(a,b) {
           var x = a.getAttribute('position');
           var y = b.getAttribute('position');
           return x == y ? 0 : ( x > y ? 1 : -1);
        });
        var pos = parseInt(items[items.length -1].getAttribute('position'))  || 0
        return pos + 1
}
//
//function doNewNode(){
//    var currPos = getNodePosition();
//    return function(target){
//            if(target.getAttribute('position')==undefined
//                    || target.getAttribute('id')==undefined){
//                switch(target.nodeName){
//
//                    break;
//                }
//            }
//    }
//
//}
//var updateNode = doNewNode();

function reposition(){
    var postContent = document.getElementById("postContent")
    var nodes = postContent.childNodes;
    var items = [];
    for(var i in nodes){
        if(nodes[i].nodeType ==1 ){
            items.push(nodes[i])
        }
    }

    items.sort(function(a,b) {
       var x = a.getAttribute('position');
       var y = b.getAttribute('position');
       return x == y ? 0 : ( x > y ? 1 : -1);
    });

    for(i = 0; i < items.length; i++){
        postContent.appendChild(items[i]);
    }

}

function addClass(node, cls){
    var nodeClass = $(node).attr('class');
    nodeClass.append(" "+cls);
    $(node).attr('class',nodeClass);

}

function removeClass(node, cls){
    var nodeClass = $(node).attr('class');

    var regex = new RegExp(""+cls+"\\s*","g");
    nodeClass = nodeClass.replace(regex, '');
    $(node).attr('class',nodeClass);

}


//
//function disableMenus(){
//    alert($("[id*='postPara']").menu("option","disabled"));
//}
//
//$("#reposition").on("click",function(e){reposition()});
//
$("#postContent").on("DOMNodeInserted", function (e) {
      var target = e.target
      updateNode(target)
});

function doUpdatePost(){
    var dateStr = document.getElementById("dateCreated").value
    var title = document.getElementById("postTitle").innerText ||
            document.getElementById("postTitle").textContent;
    var content = $('#editor').cleanHtml();
    alert(content)
    var postType = document.getElementById("postType").value
    var id = document.getElementById("id").value
    var author = document.getElementById("author").value

    alert("using update")

    jsRoutes.controllers.Application.submitBlogUpdate().ajax({
        data: {
            "id": id,
            "dateCreated": "2014-12-07",
            "title": title,
            "content": content,
            "author": author,
            "postType": postType
        },
        success: function(data){
            var d = $('<div>')
           $(d).text("Saved");
            $(d).attr('class','alert alert-success');
            $(d).attr('role','alert');
            $("#result").append(d);
        },
        error: function(data){
            var d = $('<div>')
            $(d).text("Save Failed"+ data);
            $(d).attr('class','alert alert-danger');
            $(d).attr('role','alert');
            $("#result").append(d);
        }
    });
}

function doNewPost(){
    var dateStr = document.getElementById("dateCreated").value
    var title = document.getElementById("postTitle").innerText ||
            document.getElementById("postTitle").textContent;
    var content =  $('#editor').cleanHtml();
    var postType = document.getElementById("postType").value
    var id = document.getElementById("id").value
    var author = document.getElementById("author").value

    alert("using new")
    jsRoutes.controllers.Application.submitBlog().ajax({
        data: {
            "id": id,
            "dateCreated": dateStr,
            "title": title,
            "content": content,
            "author": author,
            "postType": postType
        },
        success: function(data){
            var d = $('<div>')
           $(d).text("Saved "+data);
            $(d).attr('class','alert alert-success');
            $(d).attr('role','alert');
            $("#id").val(data);
            $("#result").append(d);

        },
        error: function(data){
            var d = $('<div>')
            $(d).text("Save Failed");
            $(d).attr('class','alert alert-danger');
            $(d).attr('role','alert');
            $("#result").append(d);
        }
    });
}

function save(){
    var id = document.getElementById("id").value
    if(id != -1){
        doUpdatePost()
    }else{
        doNewPost()
    }
}

var listId = window.getSelection().focusNode.parentNode;
                  $(listId).addClass("oder2");

/*$(function(){
    $(document).contextMenu({
        selector: "[id*='postPara']",
        callback: function(key, options) {
           doPositioning(this,key);
        },
        items: {
            "Left": {name: "Left"},
            "Center": {name: "Center"},
            "Right": {name: "Right"},
            "Float Left": {name: "Float Left"},
            "Float Right": {name: "Float Right"},
            "sep1": "---------",
            "Quit": {name: "Quit", icon: "quit"}
        }
    });
});
*/
$(function(){
    var id = document.getElementById("id").value
    if(id != -1){
        $("#saveButton").attr('value',"Update")
        jsRoutes.controllers.JsonApi.getPostById(id).ajax({
            success: function (data){
               $("#editor").html(data.content)
               $("#postTitle").text = data.title
               $("#author").attr('value', data.author)
               $("#dateCreated").attr('value', data.dateCreated)
               $("#postType").val(data.postType)
            }
        })
    }
    $("#saveButton").click(function(){ save() });
});

function doCodeFormat()
{
    var coding = 0;
    return function(){
        var listId = window.getSelection().focusNode;
        alert(listId.parentNode.id)
        if(coding == 0){
            if(listId.parentNode.id == "editor"){

                coding = 1

                $(listId).wrap('<pre><code class="java"></code></pre>')

                $('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
                window.getSelection().focusNode = listId;
            }
        }else{
             if(listId.parentNode.parentNode.parentNode.id == "editor"){
                coding = 0

                $(listId).unwrap().unwrap();
                window.getSelection().focusNode = listId;
             }
        }
    }
}

var code = doCodeFormat()

$('#editor').wysiwyg();