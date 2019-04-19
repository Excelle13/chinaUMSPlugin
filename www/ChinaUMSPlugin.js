
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'ChinaUMSPlugin', 'coolMethod', [arg0]);
};

exports.chinaUMS = function (arg0, success, error) {
    exec(success, error, 'ChinaUMSPlugin', 'chinaUMS', [arg0]);
};
