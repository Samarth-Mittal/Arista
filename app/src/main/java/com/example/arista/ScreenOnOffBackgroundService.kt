package com.example.arista

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable


class ScreenOnOffBackgroundService : Service() {
    private var screenOnOffReceiver: ScreenOnOffReceiver? = null

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        // Create an IntentFilter instance.
        val intentFilter = IntentFilter()

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")

        // Set broadcast receiver priority.
        intentFilter.priority = 100

        // Create a network change broadcast receiver.
        screenOnOffReceiver = ScreenOnOffReceiver()

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter)
        Log.d(
            ScreenOnOffReceiver.SCREEN_TOGGLE_TAG,
            "Service onCreate: screenOnOffReceiver is registered."
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister screenOnOffReceiver when destroy.
        if (screenOnOffReceiver != null) {
            unregisterReceiver(screenOnOffReceiver)
            Log.d(
                ScreenOnOffReceiver.SCREEN_TOGGLE_TAG,
                "Service onDestroy: screenOnOffReceiver is unregistered."
            )
        }
    }
}