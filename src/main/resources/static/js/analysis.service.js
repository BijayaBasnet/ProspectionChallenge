var Analysis = (function () {

    var _biAnalysisEndpoint = '/bi/analysis';

    function Analysis() {
        // No-op
    }

    Analysis.prototype = {
        getBIAnalysis: function (success, failure) {
            $.getJSON(_biAnalysisEndpoint,
                function callback(result, status, xhr) {
                    if ("success" === status) {
                        success(result);
                    } else {
                        failure(result);
                    }
                }
            );
        }
    };

    return new Analysis();
})();


