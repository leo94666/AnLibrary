package com.top.superinput.face

import android.graphics.Bitmap
import com.top.superinput.utils.dp2px
import java.io.Serializable


class Emoji : Serializable {
    var desc: String? = null
    var filter: String? = null
    var icon: Bitmap? = null
    var width = defaultSize
    var height = defaultSize

    companion object {
        private val defaultSize: Int = dp2px(32F)
    }
}
