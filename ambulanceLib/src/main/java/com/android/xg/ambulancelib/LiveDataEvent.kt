package com.android.xg.ambulancelib


class LiveDataEvent(var action: Int, var `object`: Any?) {
    companion object {
        const val ERROR = "-1"
        const val SUCCESS = "000000"
        const val LOGIN_SUCCESS = 0x01
        const val LOGIN_FAIL = 0x02

        const val DOCTORS = 0x20


    }
}
