package com.android.xg.ambulance.login

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import com.top.arch.util.AnUtils
import com.top.arch.util.NetworkUtils
import com.top.arch.util.StringUtils
import com.top.arch.util.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class ActiveActivity : BaseActivity<ActivityActiveBinding>() {

    var available: Boolean = false

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

        mDataBinding.stvActiveCode.inputEditText.setText("3a015912-760b-9dd2-c197-f3e88f193489")
        mDataBinding.fragmentActiveButton.setOnClickListener {
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


        mDataBinding.fragmentActiveButton.setOnClickListener {
            if (!available) {
                Toast.makeText(this, "请先配置网络!", Toast.LENGTH_SHORT).show()
                AnUtils.getApp().startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return@setOnClickListener
            }
            if (StringUtils.isEmpty(mDataBinding.stvCarNumber.content)) {
                Toast.makeText(this, "车牌号不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (StringUtils.isEmpty(mDataBinding.stvActiveCode.content)) {
                Toast.makeText(this, "激活码不能为空!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginViewModel?.activeDevice(
                mDataBinding.stvCarNumber.content,
                mDataBinding.stvActiveCode.content
            )
        }

        mDataBinding.ivScan.setOnClickListener {
            ScanActivity.startScanActivity(this)
        }


        loginViewModel?.eventHub?.observe(this, {
            when (it.action) {
                LiveDataEvent.LOGIN_SUCCESS -> {
                    MainActivity.startMainActivity(this)
                    finish()
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            available = NetworkUtils.isAvailable()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        keyboardUtils.hideKeyboard()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    companion object {
        /**
         * @param context
         */
        fun startActiveActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, ActiveActivity::class.java)
            context.startActivity(intent)
        }
    }
}