package com.example.nativewebview2.model

import android.util.Log
import android.webkit.JavascriptInterface
import org.json.JSONObject

class WebAppInterface {

    interface Callback {
        fun finishLoading()
    }

    private var webAppInterfaceCallback: Callback? = null

    fun setWebAppInterfaceCallback(callback: Callback?) {
        webAppInterfaceCallback = callback
    }

    @JavascriptInterface
    fun postMessage(message: String) {
        val jsonObject = JSONObject(message)
        Log.i(this.javaClass.simpleName, "postMessage: $message")
        if (jsonObject.has("type")) {
            when (jsonObject.getString("type")) {
                "finishLoading" -> webAppInterfaceCallback?.finishLoading()
            }
        }
    }

    @JavascriptInterface
    fun onMessage(message: String) {
        Log.i(this.javaClass.simpleName, "Message received from JavaScript: $message")
    }

    @JavascriptInterface
    fun onPageLoaded() {
        Log.i(this.javaClass.simpleName, "onPageLoaded")
    }
}