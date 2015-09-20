describe('Editor', function(){


    before(function(){
        $("body").append("<div id=\"revisions\"></div>");
        var stub = sinon.stub(global.jsRoutes.controllers.JsonApi,"getRevisionsWithDates");
        stub.returns({ ajax:function(data){
            return [{"commitId":"551023cd71236350f2d82cfd739fc83d0004084d","commitDate":"Sat Jun 27 15:46:55 BST 2015","extraData":""},
                {"commitId":"46dba48d7656946779744355ea3b3550244ceb5d","commitDate":"Sun Jun 21 19:13:51 BST 2015","extraData":""},
                {"commitId":"82076eec3a948073822158899ab4f6027ab3b144","commitDate":"Sun Jun 21 19:11:24 BST 2015","extraData":""}];
            }
        });
    });

    after(function(){

    });

    it("must be able to get a list of revisions", function(done){
        var editor = requirejs('editor');
        var revs = editor.getRevisions();

        return expect(revs).to.be.fulfilled.then(function(data) {
            expect(data).to.have.length(3);
            expect($('#revLink1')).to.have.$text("27/06/2015 15:46:55");
        }).should.notify(done);
    });

});