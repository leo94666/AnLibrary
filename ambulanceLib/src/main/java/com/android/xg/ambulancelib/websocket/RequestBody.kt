package com.android.xg.ambulancelib.websocket

data class RequestBody(
    var recipientIds: ArrayList<String>,
    var action: String,
    var content: Any?,
    var type: Int
)
