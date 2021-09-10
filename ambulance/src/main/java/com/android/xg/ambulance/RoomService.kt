package com.android.xg.ambulance

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.top.arch.util.ApiUtils
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class RoomService : Service() {

    companion object {
        val RoomServiceAction = "com.android.xg.ambulance.RoomService"
        val RoomServiceMessageAction = "com.android.xg.ambulance.RoomService.message"

    }

    private val TAG = "RoomService"
    private val baseWebSocket: String = "ws://118.195.158.116:8007/socket"

    private var mWebSocket: WebSocket? = null


    override fun onCreate() {
        super.onCreate()
        initRoomWebSocket()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebSocket?.cancel()
    }

    private fun initRoomWebSocket() {
        val client = OkHttpClient.Builder()
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        val request: Request =
            Request.Builder()
                .url("ws://118.195.158.116:8007/socket?userId=13524653020&&roomNo=94009983")
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
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.e(TAG, "#################onMessage String")
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

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "#################onFailure: " + response?.body().toString())

            }
        }
    }


    private fun sendMsgByBroadcast(msg: String) {
        if (msg.isBlank()) {
            return
        }
        if (mWebSocket != null) {
            val intent = Intent()
            intent.action = RoomServiceAction
            intent.putExtra(RoomServiceMessageAction, msg)
            sendBroadcast(intent)
        }
    }

}