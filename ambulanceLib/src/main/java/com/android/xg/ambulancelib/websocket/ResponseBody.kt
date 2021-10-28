package com.android.xg.ambulancelib.websocket

data class ResponseBody(
    var recipientId: String,
    var senderId: String,
    var action: String,
    var content: Any,
    var type: Int,
    var timeStamp: Long,
)
