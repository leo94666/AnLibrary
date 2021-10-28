package com.android.xg.ambulancelib.bean

data class LoginResultBean(
    var statusCode: String?,
    var statusMessage: String?,
    var data: SecretBean?
) {
    data class SecretBean(
        var sdkAppId: String?,
        var userSign: String?,
        var token: String?,
        var referToken: String,
        var accessToken: String
    )
}