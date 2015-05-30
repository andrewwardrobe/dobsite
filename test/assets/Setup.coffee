global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: __dirname
  paths : {
    "common":"mocha_require_common",
    "jquery":"lib/jquery/jquery"
  }
)

# A few modules that all tests will use
global.should = requirejs("lib/should.js/should")
global.assert = require("assert")