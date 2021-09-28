package com.android.xg.ambulance

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.android.xg.ambulance.databinding.ActivityMainBinding
import com.android.xg.ambulance.login.AmbulanceViewModel
import com.android.xg.ambulance.personal.AmbulanceProfileManager
import com.android.xg.hcnetsdk.HikVisionDeviceHelper
import com.top.arch.base.BaseActivity
import com.top.arch.util.FileUtils
import com.top.arch.utils.BaseUtils
import javax.inject.Inject


class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    var ambulanceViewModel: AmbulanceViewModel? = null
    private var TAG = "MainActivity"
    //var listOfExportsFragment: ListOfExportsFragment? = null

    override fun getLayout(): Int {
        setFullScreen()
        hideBottomUIMenu()
        return R.layout.activity_main
    }

    override fun init(root: View?) {
        HikVisionDeviceHelper.initHikVisionSDK(true)
        ambulanceViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
            //"android.permission.ACCESS_BACKGROUND_LOCATION"
        )


        //listOfExportsFragment = ListOfExportsFragment()
        mDataBinding.rl.setOnClickListener {

            //listOfExportsFragment?.show(supportFragmentManager,"www")
            ListOfExportsActivity.startListOfExportsActivity(this)
        }
        mDataBinding.includeToolbar.tvCard.text = AmbulanceProfileManager.getInstance().carNumber

        mDataBinding.includeToolbar.ivSetting.setOnClickListener {
            //listOfExportsFragment?.dismiss()
            SettingActivity.startSettingActivity(this)
        }

//        ambulanceViewModel?.eventHub.observe(this,{
//            when()
//        })
    }

    override fun onResume() {
        super.onResume()
        ambulanceViewModel?.rank()
        autoClearView()
    }

    private fun autoClearView() {

        val listFilesInDir =
            FileUtils.listFilesInDir(
                AmbulanceProfileManager.getInstance().recordVideoDirPath,
                false
            )

        if (listFilesInDir.size > 30) {
            val subList = listFilesInDir.subList(30, listFilesInDir.size)
            subList.forEach {
                FileUtils.delete(it)
            }
        }
    }

    companion object {
        /**
         * @param context
         */
        fun startMainActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }


}