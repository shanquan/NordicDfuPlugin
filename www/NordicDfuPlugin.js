var exec = require('cordova/exec');
//startDFU("C3:53:C0:39:2F:99","MI BAND","/data/user/0/com.nordicdfuexample/files/RNFetchBlobTmp4of.zip")
exports.startDFU = function(address,name,filePath,success, error) {
    exec(success, error, "NordicDfuPlugin", "startDFU", [address,name,filePath]);
};

exports.watchStateChanged = function(success, error) {
    exec(success, error, "NordicDfuPlugin", "watchStateChanged", []);
};

exports.watchProgressChanged = function(success, error) {
    exec(success, error, "NordicDfuPlugin", "watchProgressChanged", []);
};
