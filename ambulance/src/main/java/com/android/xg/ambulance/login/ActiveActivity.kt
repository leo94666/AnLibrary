package com.android.xg.ambulance.login

import android.content.Context
import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.xg.ambulance.R
import com.android.xg.ambulance.databinding.ActivityActiveBinding
import com.android.xg.ambulance.databinding.ActivityLoginBinding
import com.android.xg.ambulance.main.MainActivity
import com.android.xg.ambulance.utils.KeyboardUtil
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.LiveDataEvent
import com.elab.libarch.utils.BaseUtils
import com.elab.libarch.utils.PatternUtils
import com.top.androidx.superview.SuperAppCompatEditText
import com.top.arch.base.BaseActivity
import javax.inject.Inject


class ActiveActivity : BaseActivity<ActivityActiveBinding>() {

    @Inject
    var loginViewModel: AmbulanceViewModel? = null
    lateinit var keyboardUtils: KeyboardUtil


    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_active
    }

    override fun init(root: View?) {
        initView()
    }

    private fun initView() {

        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)
        mDataBinding.fragmentLoginButton.setOnClickListener {
            hideKeyboard(it.windowToken)
        }

        keyboardUtils = KeyboardUtil(this, mDataBinding?.stvCarNumber?.inputEditText)
        mDataBinding?.stvCarNumber?.inputEditText?.inputType = InputType.TYPE_NULL

        mDataBinding?.stvCarNumber?.inputEditText?.setOnTouchListener { v, event ->
            v.postDelayed({
                hideKeyboard(v.windowToken)
                keyboardUtils.showKeyboard()
            }, 300)
            return@setOnTouchListener false
        }

        loginViewModel?.eventHub?.observe(this, {
            when (it.action) {
                LiveDataEvent.LOGIN_SUCCESS -> {
                    MainActivity.startMainActivity(this)
                    finish()
                }
            }
        })

        mDataBinding.ivHead.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {

                return true
            }
        })
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        keyboardUtils.hideKeyboard()
    }

    companion object {
        /**
         * @param context
         */
        fun startLoginActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, ActiveActivity::class.java)
            context.startActivity(intent)
        }
    }
}