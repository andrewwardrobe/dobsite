var assert = require("assert");

var jsdom = require('jsdom').jsdom;  //Setup jsdom with some initial html
var doc = jsdom("<html><head></head><body><div id=\"leek\">leek</div></body></html>");
var window = doc.parentWindow; //We need the window from jsdom as jquery needs a window
global.window = window;     //Set the global window to the jsdom create one so we can load jquery in our requirejs modules
var $ = require("jquery");     //since we made the global window we dont need to pass the window in to jquery


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

describe('Array', function(){
  describe('#indexOf()', function(){
    console.log("hello");
    it('should twat', function(){
        console.log("hello");


        //Can stub out jsRoutes like this but probably best to use sinon
        global.jsRoutes = {
            "controllers":{
               "Application":"leeek",
            }
        };
        var leek = requirejs("leek");
        var text = leek.test("sheek");
        var jimbo = $("#leek").text();
        assert.equal(jimbo,"leek sheek" );
    });

    it("Should leek",function(){
        assert.equal("le","leek sheek" );
    });
  });

  describe('Reloading', function(){
      it('should reset the doc without breaking jquery', function(){

        doc = jsdom("<html><head></head><body><div id=\"leek\">leek</div></body></html>");
        var jimbo = doc.getElementById("leek").textContent;
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