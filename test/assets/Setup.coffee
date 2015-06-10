global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: __dirname
  paths : {
    "common":"mocha_require_common",
    "jsdom":"lib/jsdom/lib/",
    "jquery":"lib/jquery/jquery",
    "leek":"javascripts/leek",
    "jsRoutes" : "javascripts/emptyjsRoutes"
  }
)

# A few modules that all tests will use
#global.should = requirejs("lib/should.js/should")
global.assert = require("assert")
global.sinon = require("sinon")
global.chai = require("chai")
global.should = chai.should()
global.sinonChai = require("sinon-chai")
global.jsdom = require("jsdom").jsdom
chai.use(sinonChai)
#I have a emptyJsRoutes file
global.jsRoutes = requirejs("jsRoutes");
global.document = jsdom("<html><head></head><body><div id=\"leek\">leek</div></body></html>");

global.window = document.parentWindow;
global.$ = require("jquery");
