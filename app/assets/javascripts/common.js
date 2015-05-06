(function(requirejs) {
    "use strict";

    requirejs.config({
        baseUrl : "assets/javascripts",
        shim : {
            "jquery" : {
                exports : "$"
            },
            "jsRoutes" : {
                exports : "jsRoutes"
            }
        },
        paths : {
            "math" : "lib/math",
            // Map the dependencies to CDNs or WebJars directly
            "bootstrap" : "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min",
            "jsRoutes" : "//localhost:9000/javascriptRoutes",
            "jquery":"webjars/jquery/2.1.3/jquery.min",
        }
    });

    requirejs.onError = function(err) {
            console.log(err);
        };
})(requirejs);