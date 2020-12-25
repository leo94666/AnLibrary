package com.top.superinput.utils

import android.content.Context
import android.content.res.Resources

fun dp2px(context: Context, dp: Float): Int {
    val scale: Float = context.resources.displayMetrics.density

    return (dp * scale + 0.5f).toInt()
}


fun dp2px(dpValue: Float): Int {
    return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
}