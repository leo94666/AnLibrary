package com.android.xg.ambulance

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.android.xg.ambulance.databinding.ActivitySettingBinding
import com.google.gson.Gson
import com.top.androidx.badge.Badge
import com.top.androidx.vtabs.VerticalTabLayout
import com.top.androidx.vtabs.adapter.SimpleTabAdapter
import com.top.androidx.vtabs.adapter.TabAdapter
import com.top.androidx.vtabs.widget.ITabView
import com.top.androidx.vtabs.widget.TabView
import com.top.arch.base.BaseActivity
import com.top.arch.util.SPUtils
import com.top.arch.util.ToastUtils
import com.top.arch.utils.BaseUtils
import java.util.*
import kotlin.collections.ArrayList

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    var mTitles = mutableListOf("系统设置", "个人中心", "记录查询", "关于我们", "返回")

    var mDefaultDevices = mutableListOf(
        Device("183j125r73.iask.in", 20001, "admin", "bjxg12345", 1),

        Device("2v3100l327.iok.la", 8006, "admin", "bjxg12345", 1),
    )

    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_setting
    }

    override fun init(root: View?) {
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
                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.rlUser.top)
                    }
                    2 -> {
                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.rlRecord.top)
                    }

                    3 -> {
                        mDataBinding.nestedScrollView.smoothScrollTo(0, mDataBinding.rlAbout.top)
                    }
                }
            }

            override fun onTabReselected(tab: TabView?, position: Int) {

            }
        })

        initDevice()
        initListener()
    }

    private fun initListener() {

        mDataBinding.tvSetting.setOnClickListener {
            val devices = getDevice()
            SPUtils.getInstance().put("Device0", Gson().toJson(devices[0]))
            SPUtils.getInstance().put("Device1", Gson().toJson(devices[1]))
            Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show()
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

    private fun getDevice(): MutableList<Device> {
        return mutableListOf(
            Device(
                mDataBinding.etIp.text.toString(),
                mDataBinding.etPort.text.toString().toInt(),
                mDataBinding.etAdmin.text.toString(),
                mDataBinding.etPassword.text.toString(),
                mDataBinding.etChannel.text.toString().toInt()
            ),
            Device(
                mDataBinding.etIp2.text.toString(),
                mDataBinding.etPort2.text.toString().toInt(),
                mDataBinding.etAdmin2.text.toString(),
                mDataBinding.etPassword2.text.toString(),
                mDataBinding.etChannel2.text.toString().toInt()
            )
        )
    }

    private fun setDevice0(device: Device) {
        mDataBinding.etIp.setText(device.ip)
        mDataBinding.etPort.setText("" + device.port)
        mDataBinding.etAdmin.setText(device.account)
        mDataBinding.etPassword.setText(device.password)
        mDataBinding.etChannel.setText(""+device.chanel)
    }

    private fun setDevice1(device: Device) {
        mDataBinding.etIp2.setText(device.ip)
        mDataBinding.etPort2.setText("" + device.port)
        mDataBinding.etAdmin2.setText(device.account)
        mDataBinding.etPassword2.setText(device.password)
        mDataBinding.etChannel2.setText("" + device.chanel)
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