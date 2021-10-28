package com.android.xg.ambulancelib.websocket

enum class Action(val action: String) {
    MARK("Mark"),
    CLEAR_MARK("ClearMark"),
    SWITCH_SCREEN("SwitchScreen"),
    INVITE("Invite"),
    REMOTE_RESOLUTION("Resolution"),
    END_MEETING("endMeeting"),
    SSO("SSO")

}