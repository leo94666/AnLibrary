package com.android.xg.ambulancelib.bean

data class TimProfileResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: SecretBean?
) {
    data class SecretBean(
        var sdkAppId: String?,
        var userSign: String?
    )
}