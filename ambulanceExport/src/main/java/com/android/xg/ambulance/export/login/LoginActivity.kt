package com.android.xg.ambulance.export.login

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.ActivityLoginBinding
import com.android.xg.ambulance.export.main.MainActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.LiveDataEvent
import com.top.androidx.superview.SuperAppCompatEditText
import com.top.arch.base.BaseActivity
import com.top.arch.utils.BaseUtils
import com.top.arch.utils.PatternUtils
import javax.inject.Inject


class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    @Inject
    var loginViewModel: AmbulanceViewModel? = null


    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_login
    }

    override fun init(root: View?) {
        initView()
    }

    private fun initView() {

        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)
        mDataBinding.tvLoginMode.setOnClickListener {
            hideKeyboard(it.windowToken)
            val s: String = mDataBinding.tvLoginMode.text.toString()
            if (s == resources.getString(R.string.verification_code_login)) {
                mDataBinding.tvLoginMode.text = resources.getString(R.string.account_password_login)
                mDataBinding.stvPasswordVerification.setSuperEditTextMode(
                    SuperAppCompatEditText.SuperEditTextMode.SUPER_VERIFICATION,
                    resources.getString(R.string.prompt_smscode)
                )
                mDataBinding.stvPasswordVerification.exchangeVerificationStatus(true)
            } else {
                mDataBinding.tvLoginMode.text =
                    resources.getString(R.string.verification_code_login)
                mDataBinding.stvPasswordVerification.setSuperEditTextMode(
                    SuperAppCompatEditText.SuperEditTextMode.SUPER_PASS_WORD,
                    resources.getString(R.string.prompt_password)
                )
            }
        }

        mDataBinding.fragmentLoginButton.setOnClickListener {
            hideKeyboard(it.windowToken)
            val account: String = mDataBinding.stvAccount.content
            val passwordOrSmsCode: String = mDataBinding.stvPasswordVerification.content

            if (!PatternUtils.patternMatcherPhone(account)) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.empty_id_check_phone),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!mDataBinding.stvPasswordVerification.isVerificationMode) {
                //账号密码登陆
                if (passwordOrSmsCode == "") {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.empty_id_password_tip),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else {
                //短信密码登录
                if (passwordOrSmsCode == "") {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.empty_id_smscode_tip),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            val isPassword: Boolean = !mDataBinding.stvPasswordVerification.isVerificationMode
            loginViewModel?.login(
                "我是专家",
                account,
                passwordOrSmsCode,
                isPassword,
                AmbulanceViewModel.ROLE.EXPORTS
            )
        }

        mDataBinding.stvPasswordVerification.setSuperEditTextListener(object :
            SuperAppCompatEditText.SuperEditTextListener {
            override fun onChanged(view: View?, isStandard: Boolean, text: String?) {}
            override fun getImgVerificationCode(v: View) {
                hideKeyboard(v.windowToken)
                val phone: String? = mDataBinding.stvAccount.content
                if (phone == null || phone == "") {
                    Toast.makeText(
                        this@LoginActivity,
                        resources.getString(R.string.prompt_email),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (!PatternUtils.patternMatcherPhone(phone)) {
                    Toast.makeText(
                        this@LoginActivity,
                        resources.getString(R.string.empty_id_check_phone),
                        Toast.LENGTH_SHORT
                    ).show()

                    return
                }
                loginViewModel?.getCaptcha(phone, AmbulanceViewModel.Capctcha.LOGIN.ordinal)
                mDataBinding.stvPasswordVerification.startTimer()
            }
        })


        loginViewModel?.eventHub?.observe(this, {
            when (it.action) {
                LiveDataEvent.LOGIN_SUCCESS -> {
                    MainActivity.startMainActivity(this)
                    finish()
                }
            }
        })
    }


    companion object {
        /**
         * @param context
         */
        fun startLoginActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}