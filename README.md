# NordicDfuPlugin
基于[Android-DFU-Library](https://github.com/NordicSemiconductor/Android-DFU-Library)开发，仅支持Android，建议使用[cordova-plugin-ble-central-DFUPlugin](https://github.com/fxe-gear/cordova-plugin-ble-central)

## 安装

1. cordova plugin add https://gitee.com/shanquane/NordicDfuPlugin.git
2. src/android/NotificationActivity.java中的`Class.forName("yourPackageID.MainActivity")` className需要对应正确

## 使用

### startDFU
开始升级，例子
```js
startDFU("C3:53:C0:39:2F:99","MI BAND","/data/user/0/com.nordicdfuexample/files/RNFetchBlobTmp4of.zip",succeed,fail)
```

* function succeed(){} 无参数
* function fail(message){} 参数message 为字符串

### watchStateChanged
监控状态变化
* function succeed(arg){} 
参数 arg 为json对象，结构如下：
```js
{
    "state":"",
    "deviceAddress":""
}
```
状态有以下八种

* CONNECTING
* DFU_PROCESS_STARTING
* ENABLING_DFU_MODE
* FIRMWARE_VALIDATING
* DEVICE_DISCONNECTING
* DFU_COMPLETED
* DFU_ABORTED
* DFU_FAILED

* function fail(message){} 参数message 为字符串


### watchProgressChanged
监控进度变化
* function succeed(arg){} 
参数 arg 为json对象，结构如下
```js
{
    "deviceAddress":"",
    "percent":"",
    "speed":"",
    "avgSpeed":"",
    "currentPart":"",
    "partsTotal":""
}
```
* function fail(message){} 参数message 为字符串

### 代码贡献

wang.wenbin1


