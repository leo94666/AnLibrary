package com.top.androidx.graffiti

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.top.androidx.R
import com.top.androidx.graffiti.bean.SketchData
import com.top.androidx.graffiti.bean.StrokeRecord
import com.top.androidx.graffiti.dialog.BottomSheetDialog
import com.top.androidx.graffiti.view.PencilView
import com.top.arch.util.FileUtils
import com.top.arch.util.ImageUtils
import com.top.arch.util.SizeUtils
import com.top.arch.utils.BaseUtils
import kotlinx.android.synthetic.main.activity_sketch.*

public class SketchActivity : AppCompatActivity() {


    private var textPopupWindow: PopupWindow? = null
    private var popupTextLayout: View? = null
    private var strokeET: AppCompatEditText? = null //绘制文字的内容
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var defaultSavaPath: String? = null

    private var imgPath: String? = null

    companion object {

        var SAVE_PATH = "save_path"
        var IMG_PATH = "img_path"
        var IS_LAND = "is_land"
        var SEND_TO_FRIEND = 0x10009

        /**
         * @param context
         */
        fun startSketchActivity(
            context: Activity,
            isLand: Boolean,
            imgPath: String,
            savePath: String
        ) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, SketchActivity::class.java)
            intent.putExtra(IS_LAND, isLand)
            intent.putExtra(SAVE_PATH, savePath)
            intent.putExtra(IMG_PATH, imgPath)
            context.startActivityForResult(intent, SEND_TO_FRIEND)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        var isLand = intent.getBooleanExtra(IS_LAND, false)
        requestedOrientation = if (isLand) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        overridePendingTransition(R.anim.alpha_show, R.anim.alpha_hiden)

        setContentView(R.layout.activity_sketch)

        defaultSavaPath = intent.getStringExtra(SAVE_PATH)

        if (defaultSavaPath.isNullOrBlank()) {
            defaultSavaPath = "/sdcard/sketch.png"
        }




        sketch_view.setSketchData(SketchData())

        imgPath = intent.getStringExtra(IMG_PATH)
        if (FileUtils.isFileExists(imgPath)) {
            sketch_view.setBackgroundByPath(imgPath)
        }
        initView()
        initListener()
    }

    private fun initView() {
        //default is pencil
        iv_pencil.setBackgroundResource(R.drawable.ic_pencil_white)
        bottomSheetDialog = BottomSheetDialog()
        initTextPop()
    }

    private fun initListener() {
        bottomSheetDialog?.setOnClickListener(object : BottomSheetDialog.OnClickListener {
            override fun onSend() {
//                val sava = sava()
//                val intent = Intent()
//                intent.putExtra(SAVE_PATH, sava)
//                setResult(SEND_TO_FRIEND, intent)
//                finish()
                sava()?.let { share(it) }
            }

            override fun onSave() {
                sava()
            }
        })

        iv_back.setOnClickListener {
            finish()
        }

        iv_pencil.setOnClickListener {
            include_pencil.visibility = when (include_pencil.visibility) {
                View.GONE -> {
                    View.VISIBLE
                }
                View.VISIBLE -> {
                    View.GONE
                }
                else -> View.GONE
            }
        }

        iv_emoji.setOnClickListener {
            Toast.makeText(this, "emoji", Toast.LENGTH_LONG).show()
        }

        iv_cut.setOnClickListener {
            Toast.makeText(this, "cut", Toast.LENGTH_LONG).show()
        }

        iv_undo.setOnClickListener {
            sketch_view.undo()
        }

        iv_redo.setOnClickListener {
            sketch_view.redo()
        }

        iv_clear.setOnClickListener {
            sketch_view.erase()
        }

        iv_ok.setOnClickListener {
            bottomSheetDialog?.show(supportFragmentManager, "bottom_sheet_graffiti")
        }

        include_pencil.setOnSelectClickListener(object : PencilView.OnSelectClickListener {
            override fun onSelectType(type: StrokeType?) {
                sketch_view.strokeType = type

                when (type) {
                    StrokeType.STROKE_TYPE_DRAW -> {
                        iv_pencil.setBackgroundResource(R.drawable.ic_pencil_white)
                    }
                    StrokeType.STROKE_TYPE_LINE -> {
                        iv_pencil.setBackgroundResource(R.drawable.ic_line_white)
                    }
                    StrokeType.STROKE_TYPE_CIRCLE -> {
                        iv_pencil.setBackgroundResource(R.drawable.ic_circle_white)
                    }
                    StrokeType.STROKE_TYPE_RECTANGLE -> {
                        iv_pencil.setBackgroundResource(R.drawable.ic_rect_white)
                    }
                    StrokeType.STROKE_TYPE_TEXT -> {
                        iv_pencil.setBackgroundResource(R.drawable.ic_text_white)
                    }
                }
            }

            override fun onSelectStroke(progress: Int) {
                val maxPx = SizeUtils.dp2px(5f)
                val scale: Double = progress / 100.0
                sketch_view.setSize((maxPx * scale).toInt(), StrokeType.STROKE_TYPE_DRAW)
            }

            override fun onSelectColor(color: StrokeColor?) {
                sketch_view.setStrokeColor(Color.parseColor(color?.color))
            }

        })

        sketch_view.setOnDrawListener(object : SketchView.OnDrawListener {
            override fun onDrawStart() {
                cl_bottom.visibility = View.GONE
                cl_top.visibility = View.GONE
            }

            override fun onDrawEnd() {
                cl_bottom.visibility = View.VISIBLE
                cl_top.visibility = View.VISIBLE
            }

            override fun onDrawing() {

            }

        })

        sketch_view.setTextWindowCallback { view, record ->
            if (record != null) {
                if (view != null) {
                    showTextPopupWindow(view, record)
                }
            }
        }
    }


    private fun initTextPop() {
        //文本录入弹窗布局
        popupTextLayout = LayoutInflater.from(this).inflate(R.layout.popup_sketch_text, null)
        strokeET = popupTextLayout?.findViewById(R.id.text_popwindow_et) as AppCompatEditText

        textPopupWindow = PopupWindow(this)
        textPopupWindow!!.contentView = popupTextLayout
        textPopupWindow!!.width = WindowManager.LayoutParams.WRAP_CONTENT //宽度200dp
        textPopupWindow!!.height = WindowManager.LayoutParams.WRAP_CONTENT //高度自适应
        textPopupWindow!!.isFocusable = true
        textPopupWindow!!.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        textPopupWindow!!.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        textPopupWindow!!.setOnDismissListener {
            if (strokeET!!.text.toString() != "") {
                val record = StrokeRecord(sketch_view.strokeType)
                record.text = strokeET!!.text.toString()
            }
        }
    }


    @SuppressLint("WrongConstant")
    private fun showTextPopupWindow(anchor: View, record: StrokeRecord) {
        strokeET!!.requestFocus()
        textPopupWindow!!.showAsDropDown(
            anchor,
            record.textOffX,
            record.textOffY - sketch_view.height
        )
        textPopupWindow!!.softInputMode = PopupWindow.INPUT_METHOD_NEEDED
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        textPopupWindow!!.setOnDismissListener {
            if (strokeET!!.text.toString() != "") {
                record.text = strokeET!!.text.toString()
                record.textPaint.textSize = strokeET!!.textSize
                record.textWidth = strokeET!!.maxWidth
                sketch_view.addStrokeRecord(record)
            }
        }
    }


    private fun sava(): String? {
        sketch_view.isDrawingCacheEnabled = true
        val createBitmap = Bitmap.createBitmap(sketch_view.drawingCache)
        sketch_view.isDrawingCacheEnabled = false
        if (createBitmap != null) {
            ImageUtils.save(createBitmap, defaultSavaPath, Bitmap.CompressFormat.PNG)
            return defaultSavaPath
        }
        return null
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.alpha_show, R.anim.alpha_hiden)
    }


    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    fun share(imgPath: String) {
        var share_intent = Intent()
        share_intent.action = Intent.ACTION_SEND //设置分享行为
        share_intent.type = "image/jpeg" //设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgPath))
        share_intent = Intent.createChooser(share_intent, "share")
        startActivity(share_intent)
    }

}