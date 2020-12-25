package com.top.superinput.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class SoftKeyBoardUtils {

    var UI_PARAMS = "ilive_ui_params"
    var SOFT_KEY_BOARD_HEIGHT = "soft_key_board_height"

    private var softKeyBoardHeight = 0
    private var rootViewVisibleHeight //纪录根视图的显示高度
            = 0
    private var rootView //activity的根视图
            : View? = null
    private var preferences: SharedPreferences
    private var imm: InputMethodManager

    private lateinit var context: Context

    constructor(context: Context) {
        this.context = context
        preferences = context.getSharedPreferences(UI_PARAMS, Context.MODE_PRIVATE)
        imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun calculateHeight(activity: Activity) {
        val listener = OnGlobalLayoutListener {
            if (softKeyBoardHeight != 0) return@OnGlobalLayoutListener
            //获取当前根视图在屏幕上显示的大小
            val r = Rect()
            rootView!!.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return@OnGlobalLayoutListener
            }

            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                //softKeyBoardHeight =
                  //  rootViewVisibleHeight - visibleHeight - ScreenUtil.getNavigationBarHeight()
                preferences.edit().putInt(SOFT_KEY_BOARD_HEIGHT, softKeyBoardHeight)
                    .apply()
                return@OnGlobalLayoutListener
            }
        }
        //获取activity的根视图
        rootView = activity.window.decorView
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    fun getSoftKeyBoardHeight(): Int {
        if (softKeyBoardHeight != 0) return softKeyBoardHeight
        softKeyBoardHeight = preferences.getInt(SOFT_KEY_BOARD_HEIGHT, 0)
        if (softKeyBoardHeight == 0) {
            val height = screenSize[1]
            return height * 2 / 5
        }
        return softKeyBoardHeight
    }

    val screenSize: IntArray
        get() {
            val size = IntArray(2)
            val dm: DisplayMetrics = context.getResources().getDisplayMetrics()
            size[0] = dm.widthPixels
            size[1] = dm.heightPixels
            return size
        }

    fun SoftKeyboardStateHelper(activityRootView: View, listener: SoftKeyboardStateListener) {
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            activityRootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = activityRootView.rootView.height
            val heightDifference = screenHeight - r.bottom
            val visible = heightDifference > screenHeight / 3
            if (visible) {
                listener.onSoftKeyboardOpened(heightDifference)
            } else {
                listener.onSoftKeyboardClosed()
            }
        }
    }

    fun hideKeyBoard(editor: EditText) {
        imm.hideSoftInputFromWindow(editor.windowToken, 0)
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    fun showInput(et: EditText) {
        et.requestFocus()
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyBoard(token: IBinder?) {
        imm.hideSoftInputFromWindow(token, 0)
    }

    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeightInPx: Int)
        fun onSoftKeyboardClosed()
    }
}