var myApp = angular.module('myApp', ['toaster']);

myApp.controller('AppCtrl', ['$scope', '$http', 'toaster', function ($scope, $http, toaster) {
    /**
     * Initialize Chat for existing token very id and accessToken
     */
    $scope.init = function () {
        var paths = window.location.href.split('/');
        var length = paths.length;
        if (length < 2) {
            alert('UnAuthorized');
            return;
        }
        $http.post('/messages/' + paths[length - 2] + '/' + paths[length - 1]).then(function (res) {
            if (res.data.errors) {
                toaster.pop({
                    type: 'error',
                    title: 'Are you sure this is not a random link?',
                    timeout: 5000
                });
            }
            $scope.userName = res.data.response.supportDetails.userName;
            $scope.topic = res.data.response.supportDetails.topic;
            for (var i = 0; i < res.data.response.chatDetails.length; i++) {
                if (res.data.response.chatDetails.support) {
                    res.data.response.chatDetails[i].user = 'Support';
                } else {
                    res.data.response.chatDetails[i].user = 'Customer';
                }
            }
            $scope.chats = res.data.response.chatDetails;

        });
    };

}]);