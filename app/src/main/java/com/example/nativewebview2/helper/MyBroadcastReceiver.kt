package com.example.nativewebview2.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class MyBroadcastReceiver(private val context: Context, private val onReceiveAction: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.getBooleanExtra("EXIT", false) == true) {
            onReceiveAction()
        }
    }

    fun register(action: String) {
        context.registerReceiver(this, IntentFilter(action))
    }

    fun unregister() {
        context.unregisterReceiver(this)
    }
}