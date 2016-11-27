var myApp = angular.module('myApp', ['toaster']);

myApp.controller('AppCtrl', ['$scope', '$http', 'toaster', function ($scope, $http, toaster) {

    $scope.chatBox = document.getElementById('chatbox');

    $scope.urlPaths = window.location.href.split('/');
    $scope.urlLength = $scope.urlPaths.length;
    $scope.stomp = null;
    $scope.client = null;
    $scope.chats = [];

    $scope.getMessage = function(data) {
        return JSON.parse(data);
    };

    $scope.addNewChatElement = function(chat){
        $scope.chatBox.scrollTop = $scope.chatBox.scrollHeight;
        var dom = document.createElement("div");
        dom.className = 'chat ' + chat.class;
        dom.innerHTML = chat.user + ':' + chat.message;
        $scope.chatBox.appendChild(dom);
        // $scope.chats.push(chat);
    };


    $scope.startListener = function() {
        $scope.stomp.subscribe('/talk/'+ $scope.urlPaths[$scope.urlLength - 2] + '/' + $scope.urlPaths[$scope.urlLength - 1], function(data) {
            var message = $scope.getMessage(data.body);
            if (message.support) {
                message.user ='Support';
                message.class = 'support';
            }else{
                message.user ='YOU';
                message.class = 'customer';
            }
            //$scope.chats.push(message);
            $scope.addNewChatElement(message);
        });
    };

    $scope.initialize= function(){
        $scope.client = new SockJS('/chat');
        $scope.stomp = Stomp.over( $scope.client)
        $scope.stomp.connect({}, $scope.startListener);
        $scope.stomp.onclose = $scope.reconnect;
    };

    $scope.reconnect = function() {
        $timeout(function() {
            $scope.initialize();
        }, 3000);
    };




    /**
     * Initialize Chat for existing token very id and accessToken
     */
    $scope.init = function () {
        if ($scope.urlLength < 2) {
            alert('UnAuthorized');
            return;
        }
        $http.post('/messages/' + $scope.urlPaths[$scope.urlLength - 2] + '/' + $scope.urlPaths[$scope.urlLength - 1]).then(function (res) {
            if (res.data.errors) {
                toaster.pop({
                    type: 'error',
                    title: 'Are you sure this is not a random link?',
                    timeout: 5000
                });
            }
            $scope.chats = res.data.response.chatDetails;
            $scope.userName = res.data.response.supportDetails.userName;
            $scope.topic = res.data.response.supportDetails.topic;
            for (var i = 0; i < $scope.chats.length; i++) {
                if ($scope.chats[i].support) {
                    $scope.chats[i].user = 'Support';
                    $scope.chats[i].class = 'support';
                } else {
                    $scope.chats[i].class = 'customer';
                    $scope.chats[i].user = 'YOU';
                }
            }
        });

        $scope.initialize();
    };

    $scope.sendChat = function() {
        var msgToSend = $scope.chatMessage;
        if($scope.chatMessage.length==0 || $scope.chatMessage.length > 1023){
            toaster.pop({
                type: 'error',
                title: "No can't do my friend enter a non emty message or a message upto 1023 chars?",
                timeout: 5000
            });
            return;
        }
        $http.post('/message/' + $scope.urlPaths[$scope.urlLength - 2] + '/' + $scope.urlPaths[$scope.urlLength - 1],msgToSend).then(function (res) {
            if (res.data.errors) {
                toaster.pop({
                    type: 'error',
                    title: 'Message Could not be delivered!',
                    timeout: 5000
                });
            }
        });
        $scope.chatMessage = '';
    }




}]);