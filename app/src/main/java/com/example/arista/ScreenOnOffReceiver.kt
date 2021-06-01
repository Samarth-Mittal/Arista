package com.example.arista

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class ScreenOnOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_SCREEN_OFF == action) {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.")
        } else if (Intent.ACTION_SCREEN_ON == action) {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn on.")
        }
    }

    companion object {
        const val SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG"
    }
}