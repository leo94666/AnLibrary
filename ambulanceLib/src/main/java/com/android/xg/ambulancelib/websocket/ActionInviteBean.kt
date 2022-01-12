package com.android.xg.ambulancelib.websocket

import com.google.gson.Gson
import java.io.Serializable

data class ActionInviteBean(
    var roomId: String,
    var viewType: Int,
    var source: String,
    var meetId: String
) : Serializable {
    override fun toString(): String {
        return Gson().toJson(this, ActionInviteBean::class.java)
    }
}