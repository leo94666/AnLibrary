package com.top.superinput.layout

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.top.superinput.R
import com.top.superinput.base.BaseInputFragment
import com.top.superinput.face.FaceFragment
import com.top.superinput.interfaces.IInputLayout
import com.top.superinput.more.InputMoreActionUnit


abstract class InputLayoutUI : LinearLayout, IInputLayout, View.OnClickListener {

    private val STATE_NONE_INPUT = -1
    private val STATE_SOFT_INPUT = 0 //软键盘输入状态
    private val STATE_VOICE_INPUT = 1 //语音输入的状态
    private val STATE_FACE_INPUT = 2 //表情输入状态
    private val STATE_ACTION_INPUT = 3 //更多输入状态

    private var mCurrentState = 0

    private var mHostActivity: Activity? = null
    private var mFragmentManager: FragmentManager? = null

    private var mFaceFragment: FaceFragment? = null

    private lateinit var mRootView: View

    /**
     * 语音、输入转换
     */
    private lateinit var mVoiceInputSwitch: AppCompatImageView

    /**
     * 表情
     */
    private lateinit var mFaceButton: AppCompatImageView

    /**
     * 更多
     */
    private lateinit var mMoreButton: AppCompatImageView

    /**
     * 语音按住说话
     */
    private lateinit var mChatVoiceButton: AppCompatButton


    /**
     * 发送按钮
     */
    private lateinit var mChatSendButton: AppCompatButton

    /**
     * 输入框
     */
    private lateinit var mChatInput: AppCompatEditText

    private var mInputMoreView: View? = null


    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {
        mRootView = View.inflate(context, R.layout.layout_input, this)

        mVoiceInputSwitch = findViewById(R.id.iv_voice_input_switch)
        mFaceButton = findViewById(R.id.iv_face_btn)
        mMoreButton = findViewById(R.id.iv_more_btn)
        mChatSendButton = findViewById(R.id.btn_send)
        mChatInput = findViewById(R.id.et_chat_message_input)
        mChatVoiceButton = findViewById(R.id.btn_chat_voice_input)
        mInputMoreView = findViewById(R.id.more_groups)

        initListener()
    }

    private fun initListener() {

        mVoiceInputSwitch.setOnClickListener(this)
        mChatSendButton.setOnClickListener(this)
        mChatVoiceButton.setOnClickListener(this)
        mFaceButton.setOnClickListener(this)
        mChatSendButton.setOnClickListener(this)

        mChatInput.addTextChangedListener {
            //判断it长度，0是更多按钮，非0是发送按钮
            if (it?.length == 0) {
                //显示更多按钮
                mMoreButton.visibility = View.VISIBLE
                mChatSendButton.visibility = View.GONE
            } else {
                //显示发送按钮
                mMoreButton.visibility = View.GONE
                mChatSendButton.visibility = View.VISIBLE
            }
        }
    }


    override fun attachParent(
        activity: Activity?,
        childFragmentManager: FragmentManager?
    ) {
        this.mHostActivity = activity
        this.mFragmentManager = childFragmentManager
    }

    override fun disableAudioInput(disable: Boolean) {

    }

    override fun disableEmojiInput(disable: Boolean) {}
    override fun disableMoreInput(disable: Boolean) {}

    override fun replaceMoreInput(fragment: BaseInputFragment?) {}
    override fun replaceMoreInput(listener: OnClickListener?) {}

    override fun disableSendPhotoAction(disable: Boolean) {}

    override fun disableCaptureAction(disable: Boolean) {}

    override fun disableVideoRecordAction(disable: Boolean) {}

    override fun disableSendFileAction(disable: Boolean) {}

    override fun addAction(action: InputMoreActionUnit?) {}

    override val inputText: AppCompatEditText?
        get() = mChatInput

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_voice_input_switch -> {
                if (mCurrentState == STATE_VOICE_INPUT) {
                    //如果是语音输入状态，就切换到软键盘输入状态
                    mCurrentState = STATE_SOFT_INPUT
                    mVoiceInputSwitch.setImageResource(R.drawable.action_audio_selector)
                    mChatVoiceButton.visibility = View.GONE
                    mChatInput.visibility = View.VISIBLE
                    showSoftInput()
                } else {
                    //如果不是语音输入状态，就切换到语音输入状态
                    mCurrentState = STATE_VOICE_INPUT
                    mVoiceInputSwitch.setImageResource(R.drawable.action_textinput_selector)
                    mFaceButton.setImageResource(R.drawable.action_face_selector)
                    mChatVoiceButton.visibility = View.VISIBLE
                    mChatInput.visibility = View.GONE
                    hideSoftInput()
                }
            }
            R.id.iv_more_btn -> {

            }
            R.id.iv_face_btn -> {
                if (mCurrentState == STATE_VOICE_INPUT) {
                    //语音输入状态，点击表情后,要恢复输入状态
                    mCurrentState = STATE_SOFT_INPUT
                    mVoiceInputSwitch.setImageResource(R.drawable.action_audio_selector)
                    mChatVoiceButton.visibility = View.GONE
                    mChatInput.visibility = View.VISIBLE
                }
                if (mCurrentState == STATE_FACE_INPUT) {
                    //如果是表情输入状态，就切换到软键盘输入状态
                    mCurrentState = STATE_SOFT_INPUT
                    mFaceButton.setImageResource(R.drawable.action_face_selector)
                    mInputMoreView?.visibility = View.GONE
                } else {
                    //如果是软键盘输入状态，就切换到表情输入状态
                    mCurrentState = STATE_FACE_INPUT
                    mFaceButton.setImageResource(R.drawable.action_textinput_selector)
                    showFaceViewGroup()
                }
            }
        }
    }

    private fun showFaceViewGroup() {
        if (mFragmentManager == null) {
            throw Exception("you must call method attachParent first!")
        }
        if (mFaceFragment == null) {
            mFaceFragment = FaceFragment()
        }
        hideSoftInput()
        mChatInput.requestFocus()
        mInputMoreView!!.visibility = VISIBLE
        mFragmentManager!!.beginTransaction().replace(R.id.more_groups, mFaceFragment!!)
            .commitAllowingStateLoss()
    }

    private fun showMoreViewGroup() {

    }

    private fun hideSoftInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mChatInput.windowToken, 0)
        mChatInput.clearFocus()
    }

    private fun showSoftInput() {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mChatInput, 0)
    }
}
