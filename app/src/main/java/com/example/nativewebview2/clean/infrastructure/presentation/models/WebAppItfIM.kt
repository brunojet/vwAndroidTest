package com.example.nativewebview2.clean.infrastructure.presentation.models

import android.util.Log
import android.webkit.JavascriptInterface
import com.example.nativewebview2.clean.domain.models.WebAppItfDM

class WebAppItfIM {
    private val webAppInterface = WebAppItfDM(WebAppInterfaceDMCb())

    @JavascriptInterface
    fun postMessage(message: String) {
        webAppInterface.postMessage(message)
    }

    @JavascriptInterface
    fun onMessage(message: String) {
        webAppInterface.onMessage(message)
    }

    inner class WebAppInterfaceDMCb : WebAppItfDM.Callback {

        private fun simpleLog(eventName: String, message: String) {
            Log.d(this.javaClass.simpleName, "${eventName}: response from domain model -> $message")
        }

        @JavascriptInterface
        override fun postMessage(message: String) {
            simpleLog("postMessage", message)
        }

        @JavascriptInterface
        override fun onMessage(message: String) {
            simpleLog("onMessage", message)
        }
    }
}