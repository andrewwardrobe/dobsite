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


$("#saveButton").click(function(){
    var dateStr = $("#dateCreated").val();
    var title = $("#postTitle").text();
    var content = $('#editor').cleanHtml();
    var postType = $("#postType").val();
    var id = $("#postId").val();
    var author = $("#author").val();
    var json = {
           data: {
                  "id": id,
                  "dateCreated": dateStr,
                  "title": title,
                  "content": content,
                  "author": author,
                  "postType": postType
           },
           success: function(data){
               var d = $('<div>');
              $(d).text("Saved");
               $(d).attr('class','alert alert-success');
               $(d).attr('role','alert');
               $("#result").append(d);
               $("#postId").val(data);
           },
           error: function(data){
               var d = $('<div>');
               $(d).text("Save Failed"+ data);
               $(d).attr('class','alert alert-danger');
               $(d).attr('role','alert');
               $("#result").append(d);
               $("#postId").val(data);
           }
    };

    console.log(json);
    if(id == -1){
        jsRoutes.controllers.Authorised.submitBlog().ajax(json);

    }else{
        jsRoutes.controllers.Authorised.submitBlogUpdate().ajax(json);
    }


});

$(function(){
    var id = $("#postId").val();
    if(id != -1){
        $("#saveButton").attr('value',"Update");
        jsRoutes.controllers.JsonApi.getPostById(id).ajax({
            success: function (data){
               $("#editor").html(data.content);
               $("#postTitle").text = data.title;
               $("#author").attr('value', data.author);
               var d = new Date(data.dateCreated);
               var dateStr = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
               $("#dateCreated").attr('value', dateStr);
               $("#postType").val(data.postType);
            }
        });
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



$('#editor').wysiwyg();


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