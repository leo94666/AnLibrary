package com.android.xg.ambulance.http

import android.icu.text.IDNA

data class DoctorResultBean(var statusCode: String?, var statusMessage: String?, var data: ArrayList<Doctor>?) {
    data class Doctor(var userId: String?, var userName: String?, var info: DoctorInfo?)
    data class DoctorInfo(
        var nickName: String?,
        var email: String?,
        var mobile: String?,
        var sex: String?,
        var avatar: String?,
        var sign: String?
    )
}