package com.android.xg.ambulance

data class RequestBody(
    var recipientIds: ArrayList<String>,
    var action: String,
    var content: Any,
    var type: Int
)
