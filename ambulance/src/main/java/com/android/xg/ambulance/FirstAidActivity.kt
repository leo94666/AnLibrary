package com.android.xg.ambulance

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.xg.ambulance.databinding.ActivityFirstAidBinding
import com.android.xg.ambulance.databinding.ActivitySettingBinding
import com.elab.libarch.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudDef.*
import com.tencent.trtc.TRTCCloudListener
import com.top.arch.base.BaseActivity
import com.top.arch.utils.BaseUtils
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.ArrayList

class FirstAidActivity : BaseActivity<ActivityFirstAidBinding>() {
    private var TAG = "FirstAidActivity"

    private var mTRTCCloud: TRTCCloud? = null
    private var mIsCapturing = false
    private lateinit var roomServiceIntent: Intent
    private var roomMessageReceiver: RoomMessageReceiver? = null

    private var mGson: Gson? = null

    override fun getLayout(): Int {
        setFullScreen()

        return R.layout.activity_first_aid
    }

    override fun init(root: View?) {
        if (checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                //"android.permission.ACCESS_BACKGROUND_LOCATION"
            )
        ) {
            enterRoom()
            screenCapture()
        }
        lifecycle.addObserver(mDataBinding.firstAidManager)
        mGson = GsonBuilder().disableHtmlEscaping().create()
        startRoomService()
        doRegisterReceiver()
        initListener()
    }

    fun setVideoEncodeParamEx(isSoftwareCodec: Boolean) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("api", "setVideoEncodeParamEx")
            val params = JSONObject()
            params.put("codecType", if (isSoftwareCodec) 0 else 1)
            jsonObject.put("params", params)
            mTRTCCloud!!.callExperimentalAPI(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initListener() {
        mDataBinding.includeToolbar.ivSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }
    }

    private fun startRoomService() {
        roomServiceIntent = Intent(this, RoomService::class.java)
        startService(roomServiceIntent)
    }

    private fun stopRoomService() {
        stopService(roomServiceIntent)
    }

    private fun doRegisterReceiver() {
        roomMessageReceiver = RoomMessageReceiver()
        var filter = IntentFilter()
        filter.addAction(RoomService.RoomServiceAction)
        registerReceiver(roomMessageReceiver, filter)
    }

    private fun unRegisterReceiver() {
        if (roomMessageReceiver != null) {
            unregisterReceiver(roomMessageReceiver)
        }
    }

    override fun onPermissionGranted() {
        enterRoom()
        screenCapture()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitRoom()
        stopRoomService()
        unRegisterReceiver()
    }

    private inner class RoomMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == RoomService.RoomServiceAction) {
                try {
                    var msg = intent.getStringExtra(RoomService.RoomServiceMessageAction)
                    //Toast.makeText(this@FirstAidActivity, msg, Toast.LENGTH_SHORT).show()

                    Log.i(TAG, msg)
                    val messageBody = mGson?.fromJson(msg, MessageBody::class.java)

                    when (messageBody?.action) {
                        Action.MARK.action -> {
                            val actionMark =
                                mGson?.fromJson(
                                    messageBody.content.toString(),
                                    ActionMark::class.java
                                )
                            mDataBinding.drawView.drawMark(actionMark!!)
                        }
                        Action.SWITCH_SCREEN.action -> {
                            val actionSwitchScreen =
                                mGson?.fromJson(
                                    messageBody.content.toString(),
                                    ActionSwitchScreen::class.java
                                )
                            mDataBinding.firstAidManager.switchScreen(actionSwitchScreen?.value!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("", "========" + e.message.toString())
                    Toast.makeText(
                        this@FirstAidActivity,
                        "WebSocket数据解析错误:${e.toString()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }


    private fun enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(applicationContext)
        mTRTCCloud?.setListener(listener())
        val screenParams = TRTCParams()
        screenParams.sdkAppId = GenerateTestUserSig.SDKAPPID
        screenParams.userId = "13524653020"
        screenParams.roomId = 94009983
        screenParams.userSig = GenerateTestUserSig.genTestUserSig(screenParams.userId)
        screenParams.role = TRTCRoleAnchor
        mTRTCCloud?.startLocalAudio(TRTC_AUDIO_QUALITY_DEFAULT)
        setVideoEncodeParamEx(true)
        mTRTCCloud?.enterRoom(screenParams, TRTC_APP_SCENE_VIDEOCALL)
        setVideoEncodeParamEx(true)

    }

    private fun exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud!!.stopLocalAudio()
            mTRTCCloud!!.stopLocalPreview()
            mTRTCCloud!!.exitRoom()
            mTRTCCloud!!.setListener(null)
            stopScreenCapture()
        }
        mTRTCCloud = null
        TRTCCloud.destroySharedInstance()
    }

    private fun screenCapture() {
        val encParams = TRTCVideoEncParam()
        encParams.videoResolution = TRTC_VIDEO_RESOLUTION_1280_720
        encParams.videoResolutionMode = TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE
        encParams.videoFps = 10
        encParams.enableAdjustRes = false
        encParams.videoBitrate = 2000
        val params = TRTCScreenShareParams()
        setVideoEncodeParamEx(true)

        mTRTCCloud!!.startScreenCapture(TRTC_VIDEO_STREAM_TYPE_BIG, encParams, params)
        mIsCapturing = true
    }

    private fun stopScreenCapture() {
        mTRTCCloud!!.stopScreenCapture()
        mIsCapturing = false
    }

    private fun listener(): TRTCCloudListener {
        return object : TRTCCloudListener() {
            override fun onRecvCustomCmdMsg(
                userId: String?,
                cmdID: Int,
                seq: Int,
                message: ByteArray?
            ) {
                super.onRecvCustomCmdMsg(userId, cmdID, seq, message)
                ToastUtils.showToast(this@FirstAidActivity, "userId: $userId")
            }

            override fun onRecvSEIMsg(userId: String?, data: ByteArray?) {
                super.onRecvSEIMsg(userId, data)
                ToastUtils.showToast(this@FirstAidActivity, "userId: $userId")
            }

            override fun onEnterRoom(result: Long) {
                super.onEnterRoom(result)
                setVideoEncodeParamEx(true)
            }
        }

    }

    companion object {
        /**
         * @param context
         */
        fun startFirstAidActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, FirstAidActivity::class.java)
            context.startActivity(intent)
        }
    }
}
