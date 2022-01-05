package com.android.xg.ambulance.meet

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.xg.ambulance.*
import com.android.xg.ambulance.databinding.ActivityFirstAidBinding
import com.android.xg.ambulance.export.Exports
import com.android.xg.ambulance.service.RoomService
import com.android.xg.ambulance.setting.SettingActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.bean.CreateMeetingResultBean
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.android.xg.ambulancelib.websocket.*
import com.google.gson.Gson
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef.*
import com.tencent.trtc.TRTCCloudListener
import com.top.arch.base.BaseActivity
import com.top.arch.util.ScreenUtils
import com.top.arch.utils.BaseUtils
import java.util.*
import javax.inject.Inject

class FirstAidActivity : BaseActivity<ActivityFirstAidBinding>() {
    private var TAG = "FirstAidActivity"

    @Inject
    var ambulanceViewModel: AmbulanceViewModel? = null

    private var mTRTCCloud: TRTCCloud? = null
    private var mIsCapturing = false
    private lateinit var roomServiceIntent: Intent
    private var roomMessageReceiver: RoomMessageReceiver? = null
    private var mGson: Gson? = null
    private var mRoomId = ""
    private var mLastPosition = -1
    private var RoomServiceOpened = 0x11
    private var RoomServiceClosed = 0x12
    private var RoomServiceError = 0x13

    private var meetingId: String? = null
    private var startTime = 0L
    private lateinit var meetInfo: CreateMeetingResultBean.CreateMeetingBean

    //邀请人的ids
    private var inviteIds = mutableListOf<String>()

    //目前在房间的人
    private var existInviteIds = mutableListOf<String>()

    private var inviteUserIds = mutableListOf<String>()

    //进入房间的ids
    //private var enteredIds = mutableListOf<String>()

    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                RoomServiceOpened -> {
                    //Websocket 连接成功
                    mDataBinding.rlLoading.visibility = View.GONE
                    mDataBinding.drawView.visibility = View.VISIBLE
                    mDataBinding.firstAidManager.visibility = View.VISIBLE
                    mDataBinding.toolView.visibility = View.VISIBLE
                    //mDataBinding.firstAidManager.changeMode(AmbulanceProfileManager.getInstance().screenNum)

                    if (checkPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            // "android.permission.ACCESS_BACKGROUND_LOCATION"
                        )
                    ) {
                        enterRoom()
                        //screenCapture()

                        sendInvite()
                    }
                }
                RoomServiceClosed -> {

                }
                RoomServiceError -> {
                    mDataBinding.rlLoading.visibility = View.VISIBLE
                    mDataBinding.drawView.visibility = View.GONE
                    mDataBinding.firstAidManager.visibility = View.GONE

                    mDataBinding.tvLoading.setText("WebSocket链接失败.")
                }
            }
        }
    }

    private fun sendInvite() {
        val inviteBean = ActionInviteBean(
            mRoomId,
            AmbulanceProfileManager.getInstance().screenNum,
            "android"
        )
        val msg = buildWebSocketMessage(
            inviteIds as ArrayList<String>, Action.INVITE.action,
            inviteBean, 0
        )
        Log.i(TAG, "====================$msg")
        sendMessageByWebSocket(msg!!)
        ambulanceViewModel?.invite(meetingId, inviteUserIds.toTypedArray(), null)
    }


    private fun endMeet() {
        val msg = buildWebSocketMessage(
            inviteIds as ArrayList<String>, Action.END_MEETING.action,
            null, 0
        )
        Log.i(TAG, "====================$msg")
        sendMessageByWebSocket(msg!!)
    }

    private fun sendSwitchScreen(position: Int) {
        mLastPosition = position
        val actionSwitchScreen = ActionSwitchScreen(position)
        val msg = buildWebSocketMessage(
            inviteIds as ArrayList<String>, Action.SWITCH_SCREEN.action,
            actionSwitchScreen, 0
        )
        Log.i(TAG, "====================$msg")
        sendMessageByWebSocket(msg!!)
    }

    private fun sendResolution() {
        val remoteResolution = ActionRemoteResolution(
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight()
        )
        val msg = buildWebSocketMessage(
            inviteIds as ArrayList<String>, Action.REMOTE_RESOLUTION.action,
            remoteResolution, 0
        )
        Log.i(TAG, "====================$msg")
        sendMessageByWebSocket(msg!!)
    }

    override fun getLayout(): Int {
        setFullScreen()
        hideBottomUIMenu()
        keepScreenOn()
        return R.layout.activity_first_aid
    }

    override fun init(root: View?) {
        //mRoomId = (100000..999999).random()
        //Toast.makeText(this, "Room: $mRoomId",Toast.LENGTH_SHORT).show()
        ambulanceViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)
        mDataBinding.includeToolbar.tvCard.text = AmbulanceProfileManager.getInstance().carNumber
        initIntent()
        mDataBinding.includeToolbar.tvTitle.text =
            AmbulanceProfileManager.getInstance().hospital.name
        mDataBinding.includeToolbar.tvExit.visibility = View.VISIBLE
        mDataBinding.toolView.initMessage(
            AmbulanceProfileManager.getInstance().carNumber,
            AmbulanceProfileManager.getInstance().hospital.name
        )

        mDataBinding.toolView.visibility = View.GONE
        mDataBinding.toolView.setOnExitListener {
            finish()
        }
        mDataBinding.toolView.setOnSettingListener {
            SettingActivity.startSettingActivity(this)
        }
        lifecycle.addObserver(mDataBinding.firstAidManager)
        mGson = Gson()
        initListener()
        ambulanceViewModel?.createMeet(object : AmbulanceViewModel.ActionResult {
            override fun onError() {

            }

            override fun onSuccess(any: Any?) {
                if (any is CreateMeetingResultBean.CreateMeetingBean) {
                    meetInfo = any
                    mRoomId = any.roomNo!!
                    meetingId = any.meetingId
                    startRoomService()
                }
            }
        })
        //startRoomService()
        doRegisterReceiver()
    }

    private fun initIntent() {
        val export = intent.getSerializableExtra(EXPORT) as ArrayList<Exports>
        export.forEach {
            inviteIds.add(it.userPhone)
            inviteUserIds.add(it.userId)
        }
    }

    private fun initListener() {
        mDataBinding.includeToolbar.ivSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }
        mDataBinding.tvExit.setOnClickListener {
            finish()
        }

        mDataBinding.firstAidManager.setOnSelectPositionListener {
            sendSwitchScreen(it)
        }

        mDataBinding.includeToolbar.tvExit.setOnClickListener {
            finish()
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
        filter.addAction(RoomService.RoomServiceOpenedAction)
        filter.addAction(RoomService.RoomServiceClosedAction)
        filter.addAction(RoomService.RoomServiceErrorAction)

        registerReceiver(roomMessageReceiver, filter)
    }

    private fun unRegisterReceiver() {
        if (roomMessageReceiver != null) {
            unregisterReceiver(roomMessageReceiver)
        }
    }

    override fun onPermissionGranted() {
        enterRoom()
        //screenCapture()
    }

    override fun onDestroy() {
        super.onDestroy()
        endMeet()
        ambulanceViewModel?.endMeet(meetInfo, object : AmbulanceViewModel.ActionResult {
            override fun onError() {

            }

            override fun onSuccess(any: Any?) {
            }

        })
        exitRoom()
        stopRoomService()
        unRegisterReceiver()
    }

    private inner class RoomMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                RoomService.RoomServiceAction -> {
                    try {
                        var msg = intent.getStringExtra(RoomService.RoomServiceMessageAction)
                        Log.i(TAG, msg)
                        val messageBody = mGson?.fromJson(msg, ResponseBody::class.java)
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
                            Action.CLEAR_MARK.action -> {
                                mDataBinding.drawView.clearMark()
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
                RoomService.RoomServiceOpenedAction -> {
                    //websocket 链接成功
                    mHandler.sendEmptyMessage(RoomServiceOpened)
                }
                RoomService.RoomServiceClosedAction -> {
                    mHandler.sendEmptyMessage(RoomServiceClosed)
                }
                RoomService.RoomServiceErrorAction -> {
                    mHandler.sendEmptyMessage(RoomServiceError)
                }
            }
        }
    }

    private fun buildWebSocketMessage(
        recipientId: ArrayList<String>,
        action: String,
        content: Any?,
        type: Int
    ): String? {
        val requestBody = RequestBody(
            recipientId,
            action,
            content,
            type
        )
        return Gson().toJson(requestBody)
    }

    private fun sendMessageByWebSocket(msg: String) {
        val intent = Intent()
        intent.action = FirstAidAction
        intent.putExtra(FirstAidMessageAction, msg)
        sendBroadcast(intent)
    }

    private fun enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(applicationContext)
        mTRTCCloud?.setListener(listener())
        val screenParams = TRTCParams()
        screenParams.sdkAppId = AmbulanceProfileManager.getInstance().secretBean.sdkAppId?.toInt()!!
        screenParams.userId = AmbulanceProfileManager.getInstance().userId
        //screenParams.roomId = mRoomId
        screenParams.strRoomId = mRoomId
        screenParams.userSig = AmbulanceProfileManager.getInstance().secretBean.userSign
        screenParams.role = TRTCRoleAnchor
        mTRTCCloud?.startLocalAudio(TRTC_AUDIO_QUALITY_SPEECH)
        //setVideoEncodeParamEx(true)
        mTRTCCloud?.enterRoom(screenParams, TRTC_APP_SCENE_VIDEOCALL)
        startTime = System.currentTimeMillis()
        //setVideoEncodeParamEx(true)
    }

    private fun exitRoom() {
        if (mTRTCCloud != null) {
            //stopScreenCapture()
            stopLocalRecording()
            mTRTCCloud!!.stopLocalAudio()
            mTRTCCloud!!.stopLocalPreview()
            mTRTCCloud!!.exitRoom()
            mTRTCCloud!!.setListener(null)
        }
        val duration = System.currentTimeMillis() - startTime
        //Toast.makeText(this,"会诊时间: "+duration+" ms",Toast.LENGTH_SHORT).show()
        mTRTCCloud = null
        TRTCCloud.destroySharedInstance()
    }

    private fun screenCapture() {
        val encParams = TRTCVideoEncParam()
        //0.5625
        encParams.videoResolution = TRTC_VIDEO_RESOLUTION_960_720
        encParams.videoResolutionMode = TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE
        encParams.videoFps = 15
        encParams.enableAdjustRes = false
        encParams.videoBitrate = 2000
        val params = TRTCScreenShareParams()
        //setVideoEncodeParamEx(true)
        mTRTCCloud!!.startScreenCapture(TRTC_VIDEO_STREAM_TYPE_BIG, encParams, params)
        mIsCapturing = true
    }

    private fun startLocalRecording() {
        val trtcLocalRecordingParams = TRTCLocalRecordingParams()
        trtcLocalRecordingParams.filePath =
            AmbulanceProfileManager.getInstance().recordVideoSavePath
        trtcLocalRecordingParams.recordType = TRTC_RECORD_TYPE_BOTH
        mTRTCCloud!!.startLocalRecording(trtcLocalRecordingParams)
    }

    private fun stopLocalRecording() {
        mTRTCCloud!!.stopLocalRecording()
    }

    private fun stopScreenCapture() {
        mTRTCCloud!!.stopScreenCapture()
        mIsCapturing = false
    }

    private fun listener(): TRTCCloudListener {
        return object : TRTCCloudListener() {

            override fun onRemoteUserEnterRoom(userId: String?) {
                super.onRemoteUserEnterRoom(userId)
                //ToastUtils.showToast(this@FirstAidActivity, "userId: $userId")
                val contains = inviteIds.contains(userId)
                if (contains) {
                    //inviteIds.remove(userId!!)
                } else {
                    inviteIds.add(userId!!)
                }

                val contains2 = existInviteIds.contains(userId)
                if (contains2) {
                    //inviteIds.remove(userId!!)
                } else {
                    existInviteIds.add(userId!!)
                }

                sendSwitchScreen(mLastPosition)
                screenCapture()
                startLocalRecording()
                sendResolution()
            }

            override fun onRemoteUserLeaveRoom(userId: String?, reason: Int) {
                super.onRemoteUserLeaveRoom(userId, reason)
                val contains = inviteIds.contains(userId)
                if (contains) {
                    //inviteIds.remove(userId!!)
                } else {

                }
                if (inviteIds.size == 0) {
                    stopLocalRecording()
                }
            }

            override fun onUserAudioAvailable(userId: String?, available: Boolean) {
                super.onUserAudioAvailable(userId, available)
                Toast.makeText(
                    this@FirstAidActivity,
                    "userId: $userId, available: $available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {

        val EXPORT = "export"

        val FirstAidAction = "com.android.xg.ambulance.first.aid"
        val FirstAidMessageAction = "com.android.xg.ambulance.first.aid.message"

        /**
         * @param context
         */
        fun startFirstAidActivity(context: Context, mSelectedExport: ArrayList<Exports>) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, FirstAidActivity::class.java)
            intent.putExtra(EXPORT, mSelectedExport)
            context.startActivity(intent)
        }
    }
}
