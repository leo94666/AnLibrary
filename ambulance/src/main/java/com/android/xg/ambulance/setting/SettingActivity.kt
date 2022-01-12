package com.android.xg.ambulance.setting

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.xg.ambulance.R
import com.android.xg.ambulance.RecordAdapter
import com.android.xg.ambulance.databinding.ActivitySettingBinding
import com.android.xg.ambulance.login.LoginActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.bean.Device
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.google.gson.Gson
import com.top.androidx.vtabs.VerticalTabLayout
import com.top.androidx.vtabs.adapter.SimpleTabAdapter
import com.top.androidx.vtabs.widget.ITabView
import com.top.androidx.vtabs.widget.TabView
import com.top.arch.app.AppManager
import com.top.arch.base.BaseActivity
import com.top.arch.util.SPUtils
import com.top.arch.utils.BaseUtils
import javax.inject.Inject

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    var mTitles = mutableListOf("系统设置", "个人中心", "记录查询", "关于我们", "返回")
    var screenNum: Int? = null

    @Inject
    var ambulanceViewModel: AmbulanceViewModel? = null

    var mDefaultDevices = mutableListOf(
        Device(
            "183j125r73.iask.in",
            20001,
            "admin",
            "bjxg12345",
            1
        ),

        Device("2v3100l327.iok.la", 8006, "admin", "bjxg12345", 1),
    )

    override fun getLayout(): Int {
        setFullScreen()
        hideBottomUIMenu()
        keepScreenOn()

        return R.layout.activity_setting
    }

    override fun init(root: View?) {
        ambulanceViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)
        mDataBinding.includeToolbar.tvTitle.text = AmbulanceProfileManager.getInstance().hospital.name
        mDataBinding.includeToolbar.tvCard.text =AmbulanceProfileManager.getInstance().carNumber
        mDataBinding.includeToolbar.ivSetting.visibility=View.GONE
        mDataBinding.includeToolbar.tvExit.visibility=View.VISIBLE
       // mDataBinding.nestedScrollView.setScrollingEnabled(false)
        mDataBinding.tabLayout.setTabAdapter(object : SimpleTabAdapter() {
            override fun getCount(): Int {
                return mTitles.size
            }

            override fun getTitle(position: Int): ITabView.TabTitle {
                return ITabView.TabTitle.Builder().setContent(mTitles[position]).build()
            }
        })

        mDataBinding.tabLayout.addOnTabSelectedListener(object :
            VerticalTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabView?, position: Int) {
                if (position == mTitles.size - 1) {
                    finish()
                }

                when (position) {
                    0 -> {
                        mDataBinding.nestedScrollView.smoothScrollTo(0, 0)
                    }
                    1 -> {

                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.includeUser.rlUser.top)
                    }
                    2 -> {
                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.includeRecord.rlRecord.top)
                    }

                    3 -> {
                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.includeAbout.rlAbout.top)
                    }
                }
            }

            override fun onTabReselected(tab: TabView?, position: Int) {

            }
        })

        screenNum = AmbulanceProfileManager.getInstance().screenNum
        //Toast.makeText(this,""+screenNum,Toast.LENGTH_SHORT).show()

        mDataBinding.includeUser.etCarNumber.setText(AmbulanceProfileManager.getInstance().carNumber)
        mDataBinding.includeUser.etPhone.setText(AmbulanceProfileManager.getInstance().userId)
        mDataBinding.includeUser.etName.visibility = View.GONE

        //历史记录
        val recordAdapter = RecordAdapter(this)
        mDataBinding.includeRecord.rvRecord.layoutManager=LinearLayoutManager(this)

        mDataBinding.includeRecord.rvRecord.adapter=recordAdapter

        ambulanceViewModel?.historyMeet(object: AmbulanceViewModel.ActionResult{
            override fun onError() {
            }

            override fun onSuccess(any: Any?) {

                if(any is ArrayList<*>){
                    recordAdapter.addData(any as MutableList<MeetHistoryResultBean.MeetHistoryBean>)
                }
            }
        })

        initDevice()
        initListener()
    }


    private fun initListener() {

        mDataBinding.includeToolbar.tvExit.setOnClickListener {
            finish()
        }
        mDataBinding.includeSingle.tvSetting.setOnClickListener {
            val devices = getDevice()
            SPUtils.getInstance().put("Device0", Gson().toJson(devices[0]))
            if (screenNum == 4) {
                SPUtils.getInstance().put("Device1", Gson().toJson(devices[1]))
            }
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show()
        }

        mDataBinding.tvLogout.setOnClickListener {
            AmbulanceProfileManager.getInstance().logout()
            AppManager.getInstance().finishAllActivity()
            LoginActivity.startLoginActivity(this)
        }
    }

    private fun initDevice() {
        val device0 = SPUtils.getInstance().getString("Device0")
        if (device0.isBlank()) {
            setDevice0(mDefaultDevices[0])
        } else {
            val device = Gson().fromJson(device0, Device::class.java)
            if (device != null) {
                setDevice0(device)
            }
        }

        if (screenNum == 4) {
            mDataBinding.includeSingle.rlSystemSingle2.visibility = View.VISIBLE
            val devices1 = SPUtils.getInstance().getString("Device1")
            if (devices1.isBlank()) {
                setDevice1(mDefaultDevices[1])
            } else {
                val device = Gson().fromJson(devices1, Device::class.java)
                if (device != null) {
                    setDevice1(device)
                }
            }
        }

    }

    private fun getDevice(): MutableList<Device> {
        return mutableListOf(
            Device(
                mDataBinding.includeSingle.etIp.text.toString(),
                mDataBinding.includeSingle.etPort.text.toString().toInt(),
                mDataBinding.includeSingle.etAdmin.text.toString(),
                mDataBinding.includeSingle.etPassword.text.toString(),
                mDataBinding.includeSingle.etChannel.text.toString().toInt()
            ),
            Device(
                mDataBinding.includeSingle.etIp2.text.toString(),
                mDataBinding.includeSingle.etPort2.text.toString().toInt(),
                mDataBinding.includeSingle.etAdmin2.text.toString(),
                mDataBinding.includeSingle.etPassword2.text.toString(),
                mDataBinding.includeSingle.etChannel2.text.toString().toInt()
            )
        )
    }

    private fun setDevice0(device: Device) {
        mDataBinding.includeSingle.etIp.setText(device.ip)
        mDataBinding.includeSingle.etPort.setText("" + device.port)
        mDataBinding.includeSingle.etAdmin.setText(device.account)
        mDataBinding.includeSingle.etPassword.setText(device.password)
        mDataBinding.includeSingle.etChannel.setText("" + device.chanel)
    }

    private fun setDevice1(device: Device) {
        mDataBinding.includeSingle.etIp2.setText(device.ip)
        mDataBinding.includeSingle.etPort2.setText("" + device.port)
        mDataBinding.includeSingle.etAdmin2.setText(device.account)
        mDataBinding.includeSingle.etPassword2.setText(device.password)
        mDataBinding.includeSingle.etChannel2.setText("" + device.chanel)
    }

    companion object {
        /**
         * @param context
         */
        fun startSettingActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }
    }
}