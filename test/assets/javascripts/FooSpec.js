$.support.cors=true; // cross domain
$.ajaxSettings.xhr=function(){return new XMLHttpRequest();};


describe ('Array', function (){
    describe ('#indexOf()', function(){
         it('should return -1 when the value is not present', function(){
              assert.equal(true,true);
            });
    });
});

describe('Array', function(){
  describe('#indexOf()', function(){
    it('should leeeeeeeeek', function(){
      assert.equal(-1, [1,2,3].indexOf(5));
      assert.equal(-1, [1,2,3].indexOf(0));
    });
  });
});

describe('Leek', function(){


  describe('JS Test Framework', function(){

    it('should be able to mock an object',function(){
        var service = nock(sitebase)
                .get('/leek')
                .reply(200,{
                    leek:"sheek",
                    meek:"bleek"
                });




        //if we
        var leek = requirejs('leek');
        var result = leek.nockTest();
        result.then(function(){
            var text =  $("#leek").text();
            console.log("text: " +text);
            text.should.equal("sheek");
        }).fail(function(err){
                          console.log(err);
                          });


    });

    it('should be able to call function with underlying jquery', function(){
        console.log("hello");

        var leek = requirejs("leek");
        var text = leek.test("sheek");

        var jimbo = $("#leek").text();
        assert.equal(jimbo,"leek sheek" );
    });

    it("be able to stub a function",function(){

        var stub = sinon.stub(global.jsRoutes.controllers.Application, "leek");
        stub.returns("Andrew Wardrobe");

        var leek = requirejs("leek");
        leek.jsRoutesTest();
        var actualOutput = $("#leek").text();

        assert.equal(actualOutput,"Andrew Wardrobe" );
    });
  });

  describe('Reloading', function(){
      it('should reset the doc without breaking jquery', function(){

        document = jsdom("<html><head></head><body><div id=\"leek\">leek</div></body></html>");
        window.document = document;
        var jimbo = document.getElementById("leek").textContent;
        var leek = requirejs("leek");
        assert.equal(jimbo,"leek" );

        [1,2,3].indexOf(5).should.equal(-1);
        [1,2,3].indexOf(0).should.equal(-1);
      });
    });


});

describe('Array', function(){
  describe('#indexOf()', function(){
    it('should jimbo jambo', function(){
      [1,2,3].indexOf(5).should.equal(-1);
      [1,2,3].indexOf(0).should.equal(-1);
    });
  });
});