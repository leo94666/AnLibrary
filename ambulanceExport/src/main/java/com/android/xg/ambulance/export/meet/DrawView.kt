package com.android.xg.ambulance.export.meet

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.top.arch.util.ScreenUtils

class DrawView : View, View.OnTouchListener {

    private var TAG = "DrawView"
    lateinit var listener: (x: Int, y: Int) -> Unit

    private var mContext: Context? = null
    private var mRemoteWidth: Int = -1
    private var mRemoteHeight: Int = -1
    private var disable = false
    private lateinit var mView: View

    fun setOnTouchEventListener(l: (x: Int, y: Int) -> Unit) {
        this.listener = l
    }

    constructor(context: Context?) : super(context) {
        setOnTouchListener(this)
        this.mContext = context
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setOnTouchListener(this)
        this.mContext = context

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setOnTouchListener(this)
        this.mContext = context

    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (!disable) return false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                listener.invoke(-1, -1)
            }
            MotionEvent.ACTION_MOVE -> {
                //2048 1536
                if (!isClickThisView(mView, event.rawX, event.rawY)) return false
                listener.invoke(convertX(event.x), convertY(event.y))
            }
            MotionEvent.ACTION_UP -> {
                listener.invoke(-2, -2)
            }
        }
        return true
    }

    private fun isClickThisView(view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight
        return if (view.visibility != VISIBLE) {
            false
        } else y > top && y < bottom && x >= left && x <= right
    }

    fun init(width: Int, height: Int) {
        mRemoteHeight = height
        mRemoteWidth = width
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        var min = if (screenWidth > screenHeight) {
            screenHeight
        } else {
            screenWidth
        }

        var scale = if (height > width) {
            height / (width * 1.0)
        } else {
            width / (height * 1.0)
        }
        val toInt = ((scale) * min).toInt()
        val lp = this.layoutParams as RelativeLayout.LayoutParams
        if (lp.height != height || lp.width != width) {
            lp.height = min
            lp.width = toInt
            this.layoutParams = lp
        }
//        layoutParams.height = min
//        layoutParams.width = toInt
        Log.i(
            TAG,
            "===width:$width=======height: $height=======scale: $scale=======height: $min======width: $toInt"
        )
    }

    private fun convertX(x: Float): Int {
        return (((1.0 * x) / (width * 1.0)) * mRemoteWidth).toInt()
    }

    private fun convertY(y: Float): Int {
        return (((1.0 * y) / (height * 1.0)) * mRemoteHeight).toInt()
    }

    fun switch(disable: Boolean) {
        this.disable = disable
    }

    fun cal(width: Int, height: Int): IntArray {
        mRemoteHeight = height
        mRemoteWidth = width
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        var min = if (screenWidth > screenHeight) {
            screenHeight
        } else {
            screenWidth
        }

        var scale = if (height > width) {
            height / (width * 1.0)
        } else {
            width / (height * 1.0)
        }

        val toInt = ((scale) * min).toInt()
        Log.i(
            TAG,
            "===width:$width=======height: $height=======scale: $scale=======height: $min======width: $toInt"
        )
        val intArray = IntArray(2)
        intArray[0] = min
        intArray[1] = toInt
        return intArray
    }

    fun initAnchorView(view: View) {
        this.mView = view
    }


}