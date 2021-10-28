package com.android.xg.ambulance.export

import android.app.Application
import android.content.Intent
import com.android.xg.ambulance.export.service.RoomService
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.top.arch.util.AnUtils

class App : Application() {
    private var roomServiceIntent: Intent? = null


    companion object {
        private var instance: App? = null

        fun getApplication(): App? {
            return instance
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        AnUtils.init(this)
        if (AmbulanceProfileManager.getInstance().isLogin) {
            startRoomService()
        }
    }


    fun startRoomService() {
        if (roomServiceIntent == null) {
            roomServiceIntent = Intent(this, RoomService::class.java)
            startService(roomServiceIntent)
        }
    }

    fun stopRoomService() {
        if (roomServiceIntent != null) {
            stopService(roomServiceIntent)
            roomServiceIntent=null
        }
    }
}