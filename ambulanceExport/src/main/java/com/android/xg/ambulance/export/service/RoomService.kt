package com.android.xg.ambulance.export.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.android.xg.ambulancelib.websocket.Action
import com.android.xg.ambulancelib.websocket.ResponseBody
import com.google.gson.Gson
import com.top.arch.util.DeviceUtils
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class RoomService : Service() {

    companion object {
        val RoomServiceAction = "com.android.xg.ambulance.export.service.RoomService"
        val RoomServiceOpenedAction = "com.android.xg.ambulance.Open"
        val RoomServiceClosedAction = "com.android.xg.ambulance..Close"
        val RoomServiceErrorAction = "com.android.xg.ambulance.export.service.RoomService.error"

        val RoomServiceMessageAction = "com.android.xg.ambulance.export.service.RoomService.message"
        val FirstAidAction = "com.android.xg.ambulance.first.aid"
        val FirstAidMessageAction = "com.android.xg.ambulance.first.aid.message"

    }

    private var firstAidMessageReceiver: FirstAidMessageReceiver? = null


    private val TAG = "RoomService"

    private var mWebSocket: WebSocket? = null


    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "#################onCreate: ")

    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "#################onBind: ")

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "#################onStartCommand: ")

        doRegisterReceiver()
        initRoomWebSocket()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "#################onDestroy: ")
        unRegisterReceiver()
        mWebSocket?.cancel()
        mWebSocket?.close(1000, "正常关闭")
    }

    private fun doRegisterReceiver() {
        firstAidMessageReceiver = FirstAidMessageReceiver()
        var filter = IntentFilter()
        filter.addAction(FirstAidAction)
        registerReceiver(firstAidMessageReceiver, filter)
    }

    private fun unRegisterReceiver() {
        if (firstAidMessageReceiver != null) {
            unregisterReceiver(firstAidMessageReceiver)
        }
    }

    private inner class FirstAidMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == FirstAidAction) {
                var msg = intent.getStringExtra(FirstAidMessageAction)
                Log.i(TAG, msg)
                sendWebSocketMessage(msg)
            }
        }
    }


    private fun initRoomWebSocket() {
        val client = OkHttpClient.Builder()
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val macAddress = DeviceUtils.getUniqueDeviceId()
        Log.e(TAG,"唯一id: $macAddress")
        //Toast.makeText(this,macAddress,Toast.LENGTH_SHORT).show()
        val request: Request =
            Request.Builder()
                .url("ws://118.195.158.116:8007?userId=" + AmbulanceProfileManager.getInstance().userId + "&ident=" + macAddress)
                .header("Upgrade", "WebSocket")
                .addHeader("Origin", "http://stackexchange.com")
                .build()

        mWebSocket = client.newWebSocket(request, createListener())
        client.dispatcher().executorService().shutdown()

    }

    private fun createListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.e(TAG, "#################onOpen")
                send(RoomServiceOpenedAction, "open")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.e(TAG, "#################onMessage String:  $text")
                sendMsgByBroadcast(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.e(TAG, "#################onMessage ByteString")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.e(TAG, "#################onClosing:$reason")

            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.e(TAG, "#################onClosed:$reason")
                send(RoomServiceClosedAction, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "#################onFailure: " + response?.body()?.string())
                send(RoomServiceOpenedAction, response?.body().toString())
            }
        }
    }

    private fun sendWebSocketMessage(msg: String) {
        Log.e(TAG, "#################sendWebSocketMessage: msg: $msg")
        mWebSocket?.send(msg)
    }

    private fun send(action: String, msg: String) {
        Log.e(TAG, "#################send: action: $action ,msg: $msg")

        val intent = Intent()
        intent.action = action
        intent.putExtra(RoomServiceMessageAction, msg)
        sendBroadcast(intent)
    }

    private fun sendMsgByBroadcast(msg: String) {
        Log.e(TAG, "#################sendMsgByBroadcast:  $msg")

        if (msg.isBlank()) {
            return
        }
        if (mWebSocket != null) {
            val intent = Intent()
            intent.action = RoomServiceAction
            intent.putExtra(RoomServiceMessageAction, msg)
            val responseBody = Gson().fromJson(msg, ResponseBody::class.java)
            when (responseBody.action) {
                Action.SSO.action -> {
                    sendStickyBroadcast(intent)
                }
                else->{
                    sendBroadcast(intent)
                }
            }
        }
    }
}