package com.android.xg.ambulance.meet

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.xg.ambulance.R

class ToolView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private lateinit var tvExit: AppCompatTextView
    private lateinit var tvCard: AppCompatTextView
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvSetting: AppCompatImageView
    private lateinit var tvSwitch: AppCompatImageView
    private lateinit var parent: LinearLayout
    private lateinit var topPanel: ConstraintLayout

    private  var exitListener: (() -> Unit)? = null
    private  var settingListener: (() -> Unit)? =null



    private fun initView() {
        val root = LayoutInflater.from(context).inflate(R.layout.layout_tool_view, this, true)
        parent = root.findViewById(R.id.ll_parent)
        topPanel = root.findViewById(R.id.cl_top_tool)
        tvExit = root.findViewById(R.id.tv_exit)
        tvCard = root.findViewById(R.id.tv_card)
        tvTitle = root.findViewById(R.id.tv_title)
        tvSetting = root.findViewById(R.id.iv_setting)

        tvSwitch = root.findViewById(R.id.iv_panel_top_switch)

        tvSwitch.setOnClickListener {
            if (topPanel.visibility == View.VISIBLE) {
                topPanel.visibility = View.GONE
            } else {
                topPanel.visibility = View.VISIBLE
            }
        }

        tvExit.setOnClickListener {
            exitListener?.invoke()
        }

        tvSetting.setOnClickListener {
            settingListener?.invoke()
        }
    }

    public fun init(width: Int) {
        parent.layoutParams.width = width
    }

    public fun initMessage(card: String, title: String) {
        tvCard.text = card
        tvTitle.text = title
    }

    fun setOnExitListener(l:() -> Unit){
        this.exitListener=l
    }

    fun setOnSettingListener(l:() -> Unit){
        this.settingListener=l
    }


}