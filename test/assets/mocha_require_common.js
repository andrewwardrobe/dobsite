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
            "puff":"javascripts/puff",
        },

        packages:[{
            "name":"cheerio",
            "main":"index",
            "location":"lib/cheerio"
        },{
            "name":"jsdom",
            "main":"jsdom",
            "location":"lib/node-jsdom/lib"
        }]
    });

    requirejs.onError = function(err) {
            console.log(err);
        };
})(requirejs);