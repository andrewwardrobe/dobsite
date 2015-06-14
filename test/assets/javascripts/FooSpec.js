describe('Javascript Unit Testing', function(){


  describe('Examples', function(){
var service;

    before(function(){
        service = nock(sitebase).get('/leek')
                        .reply(200,{
                             leek:"sheek",
                             meek:"bleek"
                         });
    });

    after(function(){
       nock.restore();
       nock.cleanAll();
    });

    beforeEach(function(){
        $("body").append("<div id=\"leek\">leek</div>");
    });

    afterEach(function(){
        $("#leek").remove();
    });

    it('testing DOM inserts from jQuery',function(done){


            var leek = requirejs('leek');
            var result = leek.nockTest();
            result.then(function(data){
                $("#leek").text(data.leek);
            });

            return result.should.be.fulfilled.then(function(){
                expect($("#leek")).to.have.$text("sheek");
            }).should.notify(done);
    });

    it('testing Ajax with promises',function(done){
        service = nock(sitebase).get('/leek')
                .reply(200,{
                    leek:"sheek",
                    meek:"bleek"
                }).get('*').reply(404);

        var leek = requirejs('leek');
        var result = leek.nockTest();

        return expect(result).to.eventually
                 .have.property('leek',"sheek")
                 .notify(done);



    });

    it('Testing an requirejs module that uses jQuery', function(){
        var leek = requirejs("leek");
        var text = leek.test("sheek");

        var result = $("#leek").text();
        assert.equal(result,"leek sheek" );
    });

    it("stubing out jsRoutes",function(){

        //I have a jsfile with a empty js routes structure
        var stub = sinon.stub(global.jsRoutes.controllers.Application, "leek");
        stub.returns("Andrew Wardrobe");

        var leek = requirejs("leek");
        leek.jsRoutesTest();

        var actualOutput = $("#leek").text();
        assert.equal(actualOutput,"Andrew Wardrobe" );
    });

     it('making sure before and after work', function(){
            var jimbo = $("#leek").text();
            assert.equal(jimbo,"leek" );
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