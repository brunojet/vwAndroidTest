package com.example.nativewebview2.clean.infrastructure.presentation.models

import android.util.Log
import android.webkit.JavascriptInterface
import com.example.nativewebview2.clean.business.domain.models.BusinessWebApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AndroidWebAppModel {
    private val webAppInterface = BusinessWebApp(WebAppCallback())

    @Suppress("DeferredResultUnused")
    @JavascriptInterface
    fun postMessage(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            async {webAppInterface.postMessage(message)}
        }
    }

    inner class WebAppCallback : BusinessWebApp.Callback {

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
    @JavascriptInterface
    fun onMessage(message: String) {
        webAppInterface.onMessage(message)
    }

}