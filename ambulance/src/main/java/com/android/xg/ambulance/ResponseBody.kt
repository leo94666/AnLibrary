package com.android.xg.ambulance

data class ResponseBody(
    var recipientId: String,
    var senderId: String,
    var action: String,
    var content: Any,
    var type: Int,
    var timeStamp: Long,
)
