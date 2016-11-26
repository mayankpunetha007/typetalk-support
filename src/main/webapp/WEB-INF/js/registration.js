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
                toaster.pop({
                    type: 'error',
                    title: res.data.errorMessages,
                    timeout: 5000
                });
            } else {
                window.location = "/success";
            }
        });
    }
}]);