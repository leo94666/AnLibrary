package com.android.xg.ambulance

import com.google.gson.Gson

data class ActionInviteBean(var roomId: String, var viewType: Int, var source: String){
    override fun toString(): String {
        return Gson().toJson(this,ActionInviteBean::class.java)
    }
}