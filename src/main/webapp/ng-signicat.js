angular.module('ng-signicat', [])

    .directive('signicat', function ($sce) {

        var template = '<iframe ng-src="{{url}}" frameborder="0"></iframe>';

        return {
            restrict: 'E',
            template: template,
            scope: {
                host: '=host',
                service: '=service',
                method: '=method',
                profile: '=profile',
                target: '=target'
            },
            link: function(scope, el, attr) {

                var url = 'https://' + scope.host + '/std/method/' + scope.service + '?method=' + scope.method + '&profile=' + scope.profile + '&target=' + scope.target;

                el.hide();

                scope.$on('signicat-start-login', function() {
                    scope.url = $sce.trustAsResourceUrl(url);
                    el.show();
                });

                scope.loginSuccess = function() {
                    el.hide();
                    scope.url = undefined;
                    scope.$emit('signicat-login-success');
                };

                scope.loginAborted = function() {
                    el.hide();
                    scope.url = undefined;
                    scope.$emit('signicat-login-aborted');
                };



            }
        };
    });




