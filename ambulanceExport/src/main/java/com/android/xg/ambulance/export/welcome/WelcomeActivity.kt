package com.android.xg.ambulance.export.welcome

import android.Manifest
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.ActivityWelcomeBinding
import com.android.xg.ambulance.export.login.LoginActivity
import com.android.xg.ambulance.export.main.MainActivity
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.top.arch.base.BaseActivity

public class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_welcome
    }

    private var mAlphaAnimation: AlphaAnimation? = null

    override fun init(root: View?) {


        if (checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            start()
        }
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()
        //Toast.makeText(this,"onPermissionGranted", Toast.LENGTH_SHORT).show()

        start()
    }

    private fun start() {

        mAlphaAnimation = AlphaAnimation(0.1f, 1.0f)
        mAlphaAnimation?.duration = 800
        //mDataBinding.idForSplash.animation = mAlphaAnimation
        mAlphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {


            }

            override fun onAnimationEnd(animation: Animation?) {
                if (AmbulanceProfileManager.getInstance().isLogin) {
                    MainActivity.startMainActivity(this@WelcomeActivity)
                } else {
//                    MainActivity.startMainActivity(this@WelcomeActivity)
                    LoginActivity.startLoginActivity(this@WelcomeActivity)
                }
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        mDataBinding.idForSplash.startAnimation(mAlphaAnimation)

    }
}