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
    var imgName = imageId();
    var divName = "div"+imgName
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
            alert(data);
            $("#"+imgName).attr('src',data);
            }).fail(function(){
                $("#result").html(data);
            });
    //span.innerHTML = ['<img class="images" src="', e.target.result,'" title="', aFile.name, '"/>'].join('');
        document.getElementById(target).insertBefore(div, null);



         $(document).contextmenu({

      delegate: "#"+divName,
      menu: [
        {title:"Left",action: function(event,ui){ $("#"+imgName).attr('class','pull-left'); }},
        {title:"Right",action: function(event,ui){ $("#"+imgName).attr('class','pull-right'); }},
      ]
        });

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



function doNewNode(){
    var currPos = 1
    return function(target){
            if(target.getAttribute('id')==undefined){

                switch(target.nodeName){
                    case "P":
                        target.setAttribute('id','para'+currPos);
                        currPos = currPos + 1;
                        break;
                }
            }
    }

}

var updateNode = doNewNode();

$("#postContent").on("DOMNodeInserted", function (e) {
      var target = e.target
      updateNode(target)
});