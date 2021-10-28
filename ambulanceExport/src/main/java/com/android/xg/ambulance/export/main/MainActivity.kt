package com.android.xg.ambulance.export.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.xg.ambulance.export.App
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.ActivityMainBinding
import com.android.xg.ambulance.export.login.LoginActivity
import com.android.xg.ambulance.export.meet.MeetActivity
import com.android.xg.ambulance.export.service.RoomService
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.android.xg.ambulancelib.websocket.Action
import com.android.xg.ambulancelib.websocket.ActionInviteBean
import com.android.xg.ambulancelib.websocket.ResponseBody
import com.google.gson.Gson
import com.top.androidx.dialog.TopDialog
import com.top.arch.app.AppManager
import com.top.arch.base.BaseActivity
import com.top.arch.base.getNavigationFragment
import com.top.arch.util.AppUtils
import com.top.arch.utils.BaseUtils
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    var loginViewModel: AmbulanceViewModel? = null
    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun init(root: View?) {
        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        initNavigationView()
        doRegisterReceiver(RoomService.RoomServiceAction)
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        App.getApplication()?.startRoomService()
    }

    override fun onMessage(intent: Intent?) {
        super.onMessage(intent)
        when (intent?.action) {
            RoomService.RoomServiceAction -> {
                var msg = intent.getStringExtra(RoomService.RoomServiceMessageAction)
                val responseBody = Gson().fromJson(msg, ResponseBody::class.java)
                when (responseBody.action) {
                    Action.INVITE.action -> {
                        //Toast.makeText(this, "Invite", Toast.LENGTH_SHORT).show()
                        val action = Gson().fromJson(
                            responseBody.content.toString(),
                            ActionInviteBean::class.java
                        )
                        showInviteDialog(responseBody.senderId, action)
                    }
                    Action.END_MEETING.action -> {
                        //Toast.makeText(this, "end meeting", Toast.LENGTH_SHORT).show()
                        update()
                    }
                    Action.SSO.action -> {
                        removeStickyBroadcast(intent);
                        val toString = responseBody.content.toString()
                        TopDialog(this)
                            .builder()
                            .setTitle(toString)
                            .setCancelOutside(false)
                            .setNegativeButton("知道了") {
                                App.getApplication()?.stopRoomService()

                                loginViewModel?.removeId(object :AmbulanceViewModel.ActionResult{
                                    override fun onError() {

                                    }

                                    override fun onSuccess(any: Any?) {
                                        AmbulanceProfileManager.getInstance().logout()
                                        AppManager.getInstance().finishAllActivity()
                                        LoginActivity.startLoginActivity(this@MainActivity)
                                    }
                                })

                            }
                            .show()
                    }
                    else->{

                    }
                }
            }
        }
    }

    private fun update() {
        val navigationFragment = getNavigationFragment(MainFragment::class.java)
        //Toast.makeText(this, "update: $navigationFragment",Toast.LENGTH_SHORT).show()
        val meetFragment = navigationFragment?.meetFragment()
        //Toast.makeText(this, "update???: $meetFragment",Toast.LENGTH_SHORT).show()
        meetFragment?.update()
    }

    private fun showInviteDialog(senderId: String, action: ActionInviteBean) {
        TopDialog(this)
            .builder()
            .setTitle("会议邀请")
            .setNegativeButton("取消") {}
            .setPositiveButton("进入") {
                MeetActivity.startMeetActivity(this, senderId, action)
            }
            .show()
        update()
    }

    private fun initNavigationView() {
        val navController = Navigation.findNavController(this, R.id.main_layout)
        val navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_layout) as NavHostFragment
        val holdStateNavigator = FragmentNavigator(
            this,
            navigationHostFragment.childFragmentManager,
            R.id.main_layout
        )
        navController.navigatorProvider.addNavigator(holdStateNavigator)
        navController.setGraph(R.navigation.nav_main)
        NavigationUI.setupWithNavController(
            mDataBinding.bottomHomeTabs,
            navigationHostFragment.navController
        )
        //保留icon原图颜色
        mDataBinding.bottomHomeTabs.itemIconTintList = null
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

    override fun onDestroy() {
        super.onDestroy()
        //App.getApplication()?.stopRoomService()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        exitApplication()
    }

    private fun exitApplication() {
        TopDialog(this)
            .builder()
            .setTitle("退出应用")
            .setNegativeButton("取消") {}
            .setPositiveButton("确定") {
                App.getApplication()?.stopRoomService()

                AppUtils.exitApp()
            }
            .show()
    }
}