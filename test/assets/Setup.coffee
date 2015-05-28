global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: __dirname
)

# A few modules that all tests will use
global.should = requirejs("lib/should.js/should")
global.assert = require("assert")