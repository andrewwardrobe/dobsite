(function(requirejs) {
    "use strict";

    requirejs.config({
        baseUrl : "/assets/javascripts",
        waitSeconds : 0,
        shim : {
            "jquery" : {
                exports : "$"
            },
            "jsRoutes" : {
                exports : "jsRoutes"
            },
            "Q": {
                exports: "Q"
            },
            "bootstrap" : { "deps" :['jquery'] },
            "wysiwyg" : {"deps":['jquery']},
            "wysiwyg-editor" : {"deps":['jquery','wysiwyg']}
        },

        paths : {
            "math" : "lib/math",
            // Map the dependencies to CDNs or WebJars directly
            "bootstrap" : "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min",
            "jsRoutes" : "javascriptRoutes",
            "highlight": "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/highlight.min.js",
            "jquery":"webjars/jquery/2.1.4/jquery.min",
            "jquery-ui":"webjars/jquery-ui/1.11.4/jquery-ui.min",
            "q" : "webjars/q/1.1.2/q"
        }
    });

    requirejs.onError = function(err) {
            console.log(err.toString());
            console.log(JSON.stringify(err,null,2));

        };


})(requirejs);

sitebase = document.location.origin;