package com.android.xg.ambulance.welcome

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.android.xg.ambulance.main.MainActivity
import com.android.xg.ambulance.R
import com.android.xg.ambulance.databinding.ActivityWelcomeBinding
import com.android.xg.ambulance.login.ActiveActivity
import com.android.xg.ambulance.login.LoginActivity
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.top.arch.base.BaseActivity

public class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_welcome
    }

    override fun init(root: View?) {
        val mAlphaAnimation = AlphaAnimation(0.1f, 1.0f)
        mAlphaAnimation.duration = 800
        mDataBinding.idForSplash.animation = mAlphaAnimation
        mAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {


            }

            override fun onAnimationEnd(animation: Animation?) {
                if (AmbulanceProfileManager.getInstance().isLogin) {
                    MainActivity.startMainActivity(this@WelcomeActivity)
                } else {
                    LoginActivity.startLoginActivity(this@WelcomeActivity)
                    //ActiveActivity.startLoginActivity(this@WelcomeActivity)
                }
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }
}