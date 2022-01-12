package com.android.xg.ambulance.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.android.xg.ambulance.R
import com.android.xg.ambulance.databinding.ActivityScanBinding
import com.elab.libarch.utils.BaseUtils
import com.elab.libarch.utils.ToastUtils
import com.top.arch.base.BaseActivity


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ScanActivity : BaseActivity<ActivityScanBinding>() {

    private var SCAN_RESULT_CODE = 1002;

    override fun getLayout(): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //设置不息屏

        return R.layout.activity_scan
    }

    override fun init(root: View) {
        supportActionBar?.hide()
        mDataBinding.scannerView.setOnScannerResultListener({ success, result ->
            if (success) {
                //ToastUtils.showToast(this, result)
                //ScanLoginActivity.startScanLoginActivity(this)
                setResult(SCAN_RESULT_CODE)
                finish()
            }
        }, {
            finish()
        })
    }


    companion object {
        /**
         * @param context
         */
        fun startScanActivity(
            context: Activity
        ) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(
                context,
                ScanActivity::class.java
            )

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.alpha_show, R.anim.alpha_hiden)
        }
    }
}