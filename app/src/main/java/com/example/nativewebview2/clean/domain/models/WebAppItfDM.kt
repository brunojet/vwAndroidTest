package com.example.nativewebview2.clean.domain.models

import android.util.Log
import org.json.JSONObject

class WebAppItfDM(private val cb: Callback) {
    interface Callback {
        fun postMessage(message: String)
        fun onMessage(message: String)
    }

    private fun simpleLog(eventName: String) {
        Log.d(
            this.javaClass.simpleName, "${eventName}: request from infrastructure to domain model"
        )
    }

    fun postMessage(message: String) {
        simpleLog("postMessage")

        try {
            val jsonObject = JSONObject(message)

            if (jsonObject.has("type")) {
                when (jsonObject.getString("type")) {
                    "finishLoading" -> ActionTriggersDM.webViewLoaded.postValue(true)
                }
                cb.postMessage("Ready!")
            } else {
                cb.postMessage("Received invalid message payload: $message")
            }
        } catch (e: Exception) {
            cb.postMessage("Received invalid message caused by: ${e.message}")
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMessage(message: String) {
        simpleLog("onMessage")
        cb.onMessage("Ready!")
    }
}
