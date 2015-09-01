angular.module('app', ['ng-signicat'])

    .controller('AppController', function ($scope, $http) {

        $scope.loadUsername = function () {
            $http.get('rest/username')
                .success(function (data) {
                    $scope.msg = data;
                })
                .error(function () {
                    $scope.msg = 'Failed to get username. You are probably not logged in.';
                });
        };

        $scope.logout = function () {
            $http.get('rest/logout').success(function (data) {
                $scope.msg = data;
            });
        };

        $scope.$on('signicat-login-success', function() {
            $scope.loadUsername();
        });
    })

    .config(function ($httpProvider) {
        $httpProvider.interceptors.push(function ($rootScope, $q) {
            return {
                responseError: function (rejection) {
                    if (rejection.status == 401) {
                        $rootScope.$broadcast('signicat-start-login');
                    }
                    return $q.reject(rejection);
                }
            };
        });
    });



