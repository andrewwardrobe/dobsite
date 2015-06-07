global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: __dirname
  paths : {
    "common":"mocha_require_common",
    "jquery":"lib/jquery/jquery",
    "utilities":"javascripts/utilities",
    "leek":"javascripts/leek",
    "jsdom":"lib/jsdom/lib"
  }
)

# A few modules that all tests will use
global.should = requirejs("lib/should.js/should")
global.assert = require("assert")
