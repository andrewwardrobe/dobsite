global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: __dirname
  paths : {
    "common":"mocha_require_common",
    "jsdom":"lib/jsdom/lib/",
    "jquery":"lib/jquery/jquery",
    "leek":"javascripts/leek",
    "jsRoutes" : "javascripts/emptyjsRoutes",
    "q" : "lib/q"
  }
)

# A few modules that all tests will use
#global.should = requirejs("lib/should.js/should")
global.assert = require("assert")
global.sinon = require("sinon")

#Set up chai assertions
global.chai = require("chai")
global.should = chai.should()
global.sinonChai = require("sinon-chai")
global.jsdom = require("jsdom").jsdom
chai.use(sinonChai)
global.chaiJQ = require("chai-jq")
chai.use(chaiJQ)
global.chaiAsPromised = require("chai-as-promised")
chai.use(chaiAsPromised)
global.expect = chai.expect

#I have a emptyJsRoutes file so I can mock it
global.jsRoutes = requirejs("jsRoutes")

#Request mocking
global.nock = require("nock")

#Setup dom testing from node
global.document = jsdom()
global.XMLHttpRequest = require('xmlhttprequest').XMLHttpRequest
global.window = document.parentWindow
global.$ = require("jquery")
$.support.cors = true
$.ajaxSettings.xhr = () ->
  return new XMLHttpRequest()


#I use this a so I can test my ajax
global.sitebase = "https://www.daoostinboyeez.com"
global.Q = require('q')





