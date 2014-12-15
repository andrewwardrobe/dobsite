function updateTitle(){
    var titleContent = document.getElementById("title").value;
    document.getElementById("postTitle").innerHTML = titleContent;
}

function updateTitleBox(){
    var titleContent = document.getElementById("postTitle").innerText ||
        document.getElementById("postTitle").textContent;
    document.getElementById("title").value = titleContent;
}
function p(t){
    t = t.trim();
    return (t.length>0?'<p>'+t.replace(/[\r\n]+/,'</p><p>')+'</p>':null);
}

function updateContent(){
    var blogContent = document.getElementById("content").value;
    document.getElementById("postContent").innerHTML = p(blogContent);
}

function updateContentBox(){
    var blogContent = document.getElementById("postContent").innerHTML;
    document.getElementById("content").value = blogContent;
    document.getElementById("result").textContent = blogContent;
}


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

function doNewNode(){
    var currPos = getNodePosition();
    return function(target){
            console.log("currPos:" +currPos);
            if(target.getAttribute('position')==undefined
                    || target.getAttribute('id')==undefined){
                switch(target.nodeName){
                    case "P":
                        var name =  'postPara-'+currPos;
                        target.setAttribute('position',currPos);
                        target.setAttribute('id',name);
                        currPos = currPos + 1;
                        break;
                    case "DIV":
                        target.setAttribute('position',currPos);
                        currPos = currPos + 1;
                        target.setAttribute('position','display: inline-block');
                        break;
                }
            }
    }

}


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
var updateNode = doNewNode();

function disableMenus(){
    alert($("[id*='postPara']").menu("option","disabled"));
}

$("#reposition").on("click",function(e){reposition()});

$("#postContent").on("DOMNodeInserted", function (e) {
      var target = e.target
      updateNode(target)
});

function doPositioning(node, key){
    switch(key){
        case "Left":
            $(node).attr('class','moveLeft');
            break;
        case "Center":
            $(node).attr('class','moveCenter');
            break;
        case "Right":
            $(node).attr('class','moveRight');
            break;
         case "Float Left":
            $(node).attr('class','pull-left');
            break;
        case "Float Right":
            $(node).attr('class','pull-right');
            break;
    }
}




$(function(){
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
            "quit": {name: "Quit", icon: "quit"}
        }
    });
});
