package com.example.nativewebview2.clean.data.domain.abstractions

import android.util.Log

abstract class WebAppAbstraction {

    private fun simpleLog(eventName: String) {
        Log.d(
            this.javaClass.simpleName, "Event $eventName received!"
        )
    }

    open fun postMessage(message: String) {
        simpleLog("postMessage")

    }

    open fun onMessage(message: String) {
        simpleLog("onMessage")
    }
}