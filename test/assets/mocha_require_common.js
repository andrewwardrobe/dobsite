(function(requirejs) {
    "use strict";

    requirejs.config({
        baseUrl : "target/web/public/test/public",
        paths : {
            "math" : "lib/math",
            // Map the dependencies to CDNs or WebJars directly
            "bootstrap" : "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min",
            "jsRoutes" : "javascriptRoutes",
            "jquery":"lib/jquery/jquery",
            "utilities":"javascripts/utilities",
            "cheerio":"lib/cheerio/lib/cheerio",
            "parse":"lib/cheerio/lib/parse",
            "utils":"lib/cheerio/lib/utils",
            "static":"lib/cheerio/lib/static",
            "attributes":"lib/cheerio/lib/api/attributes",
            "css":"lib/cheerio/lib/api/css",
            "forms":"lib/cheerio/lib/api/forms",
            "manipulation":"lib/cheerio/lib/api/manipulation",
            "transversing":"lib/cheerio/lib/api/transversing"
        }
    });

    requirejs.onError = function(err) {
            console.log(err);
        };
})(requirejs);