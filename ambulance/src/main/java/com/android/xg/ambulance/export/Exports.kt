package com.android.xg.ambulance.export

import java.io.Serializable

data class Exports(
    var userPhone: String,
    var userId: String,
    var name: String,
    var avatar: String,
    var certification: Boolean,
    var score: Int,
    var isSelected:Boolean=false
) : Serializable
