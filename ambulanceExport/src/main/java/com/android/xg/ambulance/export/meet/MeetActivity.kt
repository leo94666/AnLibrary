package com.android.xg.ambulance.export.meet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.ActivityMeetBinding
import com.android.xg.ambulance.export.service.RoomService
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.android.xg.ambulancelib.websocket.*
import com.google.gson.Gson
import com.tencent.liteav.TXLiteAVCode
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudDef.TRTCParams
import com.tencent.trtc.TRTCCloudDef.TRTCVideoEncParam
import com.tencent.trtc.TRTCCloudListener
import com.top.androidx.dialog.TopDialog
import com.top.arch.base.BaseActivity
import com.top.arch.util.ScreenUtils
import com.top.arch.utils.BaseUtils

class MeetActivity : BaseActivity<ActivityMeetBinding>() {
    private var mTRTCCloud: TRTCCloud? = null
    private var mRoomId = ""
    private var mSenderId = ""
    private var mType = 2
    private var mSource = ""

    private var audioDefaultOn = false


    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.activity_meet
    }

    override fun init(root: View?) {
        initIntent()
        doRegisterReceiver(RoomService.RoomServiceAction)
        initVideoView()
        initView()
        initListener()
        if (
            checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            enterRoom()
        }
        mDataBinding.drawView.setOnTouchEventListener { x, y ->
            var r = RequestBody(
                mutableListOf(mSenderId) as ArrayList<String>,
                Action.MARK.action,
                ActionMark(floatArrayOf(x.toFloat(), y.toFloat())),
                0
            )
            val toJson = Gson().toJson(r)
            sendMessage(RoomService.FirstAidMessageAction, toJson)
        }
    }

    private fun initView() {
        if (mType == 2) {
            mDataBinding.ivLeftTop.visibility = View.GONE
            mDataBinding.ivLeftBottom.visibility = View.GONE
            mDataBinding.ivRightBottom.visibility = View.GONE
            mDataBinding.ivRightTop.visibility = View.GONE
            mDataBinding.ivSwitch.visibility = View.VISIBLE
        } else if (mType == 4) {
            mDataBinding.ivLeftTop.visibility = View.VISIBLE
            mDataBinding.ivLeftBottom.visibility = View.VISIBLE
            mDataBinding.ivRightBottom.visibility = View.VISIBLE
            mDataBinding.ivRightTop.visibility = View.VISIBLE
            mDataBinding.ivSwitch.visibility = View.GONE
        }
    }

    private fun initListener() {
        mDataBinding.tvExit.setOnClickListener {
            finish()
        }
        mDataBinding.ivLeftTop.setOnClickListener {
            sendToggleButton(it, 0)
        }
        mDataBinding.ivRightTop.setOnClickListener {
            sendToggleButton(it, 1)
        }
        mDataBinding.ivLeftBottom.setOnClickListener {
            sendToggleButton(it, 2)
        }
        mDataBinding.ivRightBottom.setOnClickListener {
            sendToggleButton(it, 3)
        }

        mDataBinding.ivSwitch.setOnClickListener {
            sendToggleButton(it, 5)
        }
        mDataBinding.ivPencil.setOnClickListener {
            mDataBinding.ivPencil.isSelected = !mDataBinding.ivPencil.isSelected
            mDataBinding.drawView.switch(mDataBinding.ivPencil.isSelected)
            mDataBinding.drawView.initAnchorView(mDataBinding.liveCloudRemoteScreenshare)
            if (!mDataBinding.ivPencil.isSelected) {
                val requestBody =
                    RequestBody(arrayListOf(mSenderId), Action.CLEAR_MARK.action, null, 0)
                var msg = Gson().toJson(requestBody)
                sendMessage(RoomService.FirstAidMessageAction, msg)
            }
        }
        mDataBinding.ivMic.isSelected = audioDefaultOn
        mDataBinding.ivMic.setOnClickListener {
            mDataBinding.ivMic.isSelected = !mDataBinding.ivMic.isSelected
            val selected = mDataBinding.ivMic.isSelected
            //Toast.makeText(this, "sel: $selected", Toast.LENGTH_SHORT).show()
            mTRTCCloud?.muteLocalAudio(!selected)
        }
    }


    private fun sendMessage(key: String?, msg: String?) {
        sendMessage(RoomService.FirstAidAction, key, msg)
    }

    override fun onMessage(intent: Intent?) {
        super.onMessage(intent)
        when (intent?.action) {
            RoomService.RoomServiceAction -> {
                var msg = intent.getStringExtra(RoomService.RoomServiceMessageAction)
                val responseBody = Gson().fromJson(msg, ResponseBody::class.java)
                when (responseBody.action) {
                    Action.REMOTE_RESOLUTION.action -> {
                        val action = Gson().fromJson(
                            responseBody.content.toString(),
                            ActionRemoteResolution::class.java
                        )
                        mDataBinding.drawView.init(action.width, action.height)
//                        val cal = mDataBinding.drawView.cal(action.width, action.height)
//                        setViewLayoutParams(mDataBinding.drawView,cal[0],cal[1])
                    }
                    Action.SWITCH_SCREEN.action -> {
                        val action = Gson().fromJson(
                            responseBody.content.toString(),
                            ActionSwitchScreen::class.java
                        )
                        toggleButton(action.value)
                    }
                }
            }
        }
    }


    private fun sendToggleButton(v: View, position: Int) {
        if (mType == 2) {
            v.isSelected = !v.isSelected
            sendToggleButtonMessage(5)
        } else if (mType == 4) {
            v.isSelected = !v.isSelected
            if (v.isSelected) {
                sendToggleButtonMessage(position)
                toggleButton(position)
            } else {
                sendToggleButtonMessage(4)
            }
        }
    }

    private fun sendToggleButtonMessage(position: Int) {
        var msg = Gson().toJson(
            RequestBody(
                arrayListOf(mSenderId),
                Action.SWITCH_SCREEN.action,
                ActionSwitchScreen(position),
                0
            )
        )
        sendMessage(RoomService.FirstAidMessageAction, msg)
    }

    private fun toggleButton(position: Int) {
        when (position) {
            0 -> {
                //左上角
                mDataBinding.ivLeftTop.isSelected = true
                mDataBinding.ivRightTop.isSelected = false
                mDataBinding.ivLeftBottom.isSelected = false
                mDataBinding.ivRightBottom.isSelected = false
            }
            1 -> {
                //右上角
                mDataBinding.ivLeftTop.isSelected = false
                mDataBinding.ivRightTop.isSelected = true
                mDataBinding.ivLeftBottom.isSelected = false
                mDataBinding.ivRightBottom.isSelected = false
            }
            2 -> {
                //左下角
                mDataBinding.ivLeftTop.isSelected = false
                mDataBinding.ivRightTop.isSelected = false
                mDataBinding.ivLeftBottom.isSelected = true
                mDataBinding.ivRightBottom.isSelected = false
            }
            3 -> {
                //右下角
                mDataBinding.ivLeftTop.isSelected = false
                mDataBinding.ivRightTop.isSelected = false
                mDataBinding.ivLeftBottom.isSelected = false
                mDataBinding.ivRightBottom.isSelected = true
            }
            4 -> {
                //恢复
                mDataBinding.ivLeftTop.isSelected = false
                mDataBinding.ivRightTop.isSelected = false
                mDataBinding.ivLeftBottom.isSelected = false
                mDataBinding.ivRightBottom.isSelected = false
            }
            5 -> {
                //二分屏切换
                mDataBinding.ivSwitch.isSelected = !mDataBinding.ivSwitch.isSelected
            }
            else -> {
                mDataBinding.ivLeftTop.isSelected = false
                mDataBinding.ivRightTop.isSelected = false
                mDataBinding.ivLeftBottom.isSelected = false
                mDataBinding.ivRightBottom.isSelected = false
            }
        }
    }


    private fun initVideoView() {
        var min = if (ScreenUtils.getScreenWidth() < ScreenUtils.getScreenHeight()) {
            ScreenUtils.getScreenWidth()
        } else {
            ScreenUtils.getScreenHeight()
        }
        mDataBinding.liveCloudRemoteScreenshare.layoutParams.height = min
        mDataBinding.liveCloudRemoteScreenshare.layoutParams.width = (min * (4 / 3.0)).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitRoom()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }


    private fun enterRoom() {
        mTRTCCloud = TRTCCloud.sharedInstance(this)
        mTRTCCloud?.setListener(listener())
        val mTRTCParams = TRTCParams()
        mTRTCParams.sdkAppId = AmbulanceProfileManager.getInstance().secretBean.sdkAppId?.toInt()!!
        mTRTCParams.userId = AmbulanceProfileManager.getInstance().userId
        mTRTCParams.strRoomId = mRoomId
        mTRTCParams.userSig = AmbulanceProfileManager.getInstance().secretBean.userSign
        mTRTCParams.role = TRTCCloudDef.TRTCRoleAnchor
        mTRTCCloud?.enableAudioVolumeEvaluation(2000)
        mTRTCCloud?.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH)
        mTRTCCloud?.enterRoom(mTRTCParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL)
        mTRTCCloud?.muteLocalAudio(!audioDefaultOn)
    }

    private fun exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud!!.stopLocalAudio()
            mTRTCCloud!!.stopLocalPreview()
            mTRTCCloud!!.exitRoom()
            mTRTCCloud!!.setListener(null)
        }
        mTRTCCloud = null
        TRTCCloud.destroySharedInstance()
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()
        enterRoom()
    }

    private fun initIntent() {
        val invite = intent.getSerializableExtra(INVITE) as ActionInviteBean
        mRoomId = invite.roomId
        mSenderId = intent.getStringExtra(SENDER_ID)
        mType = invite.viewType
        mSource = invite.source
        //Toast.makeText(this, ": $mSource",Toast.LENGTH_SHORT).show()
    }

    private fun showEndDialog() {
        TopDialog(this)
            .builder()
            .setTitle("会议结束")
            .setPositiveButton("知道了") {
                finish()
            }
            .show()
    }

    private fun listener(): TRTCCloudListener {
        return object : TRTCCloudListener() {

            override fun onRemoteUserEnterRoom(userId: String?) {
                super.onRemoteUserEnterRoom(userId)
            }

            override fun onRemoteUserLeaveRoom(userId: String?, reason: Int) {
                super.onRemoteUserLeaveRoom(userId, reason)
                if (userId.equals(mSenderId)) {
                    showEndDialog()
                }
            }

            override fun onUserVideoAvailable(userId: String, available: Boolean) {
                if (available) {
                    mDataBinding.liveCloudRemoteScreenshare.visibility = View.VISIBLE
                    val encParams = TRTCVideoEncParam()
                    encParams.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080
                    encParams.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE
                    encParams.videoFps = 15
                    encParams.enableAdjustRes = false
                    encParams.videoBitrate = 1500
                    mTRTCCloud!!.setVideoEncoderParam(encParams)
                    if (mSource == "android") {
                        mTRTCCloud!!.startRemoteView(
                            userId,
                            TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG,
                            mDataBinding.liveCloudRemoteScreenshare
                        )
                    } else if (mSource == "pc") {
                        mTRTCCloud!!.startRemoteView(
                            userId,
                            TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB,
                            mDataBinding.liveCloudRemoteScreenshare
                        )
                    }
                } else {
                    mDataBinding.liveCloudRemoteScreenshare.visibility = View.INVISIBLE
                    mTRTCCloud!!.stopRemoteView(userId)
                }
            }

            override fun onError(errCode: Int, errMsg: String, extraInfo: Bundle?) {
                Toast.makeText(this@MeetActivity, "onError: $errMsg[$errCode]", Toast.LENGTH_SHORT)
                    .show()
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    exitRoom()
                }
            }
        }
    }

    companion object {

        var INVITE = "MEET_INVITE"
        var SENDER_ID = "MEET_SENDER_ID"

        /**
         * @param context
         */
        fun startMeetActivity(context: Context, senderId: String, action: ActionInviteBean) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, MeetActivity::class.java)
            intent.putExtra(INVITE, action)
            intent.putExtra(SENDER_ID, senderId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}