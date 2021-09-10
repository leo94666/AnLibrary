package com.android.xg.ambulance

import android.os.Handler
import android.view.View
import com.android.xg.ambulance.databinding.ActivityMainBinding
import com.android.xg.hcnetsdk.HikVisionDeviceHelper
import com.top.arch.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var listOfExportsFragment: ListOfExportsFragment? = null

    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_main
    }

    override fun init(root: View?) {
        HikVisionDeviceHelper.initHikVisionSDK(true)

        listOfExportsFragment = ListOfExportsFragment()
        mDataBinding.rl.setOnClickListener {

            //listOfExportsFragment?.show(supportFragmentManager,"www")
            ListOfExportsActivity.startListOfExportsActivity(this)
        }

        mDataBinding.includeToolbar.ivSetting.setOnClickListener {
            //listOfExportsFragment?.dismiss()
            SettingActivity.startSettingActivity(this)
        }
    }


}