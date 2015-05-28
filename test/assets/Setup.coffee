global.requirejs = require("requirejs")

requirejs.config(
  nodeRequire: require
  baseUrl: "assets/javascripts"
)

# A few modules that all tests will use
global.should = requirejs("webjars/should.js/5.0.0/should")
global.assert = require("assert")