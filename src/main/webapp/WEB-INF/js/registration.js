/**
 * Created by mayan on 11/22/2016.
 */
var app = angular.module('support', ['toaster']);

app.controller('reg', ['$scope', '$http', 'toaster', function ($scope, $http, toaster) {

    $scope.sendEmail = function () {
        $http.post('/register/ticket', {
            'email': $scope.email,
            'requestTopic': $scope.topic,
            'name': $scope.name
        }).then(function (res) {
            if (res.data.errors) {
                var errors = res.data.errorMessages;
                for(var i=0;i<errors.length;i++) {
                    toaster.pop({
                        type: 'error',
                        title: errors[i],
                        timeout: 5000
                    });
                }
            } else {
                if(res.data.response == 'success') {
                    window.location = "/success";
                }else {
                    window.location = "/support/"+res.data.response.id+"/"+res.data.response.accessKey;
                }
            }
        });
    }
}]);