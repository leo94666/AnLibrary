package com.android.xg.ambulancelib.bean

data class ActiveDeviceResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: SecretBean?
) {
    data class SecretBean(
        var expireTime: String?,
        var referToken: String,
        var accessToken: String
    )
}