package com.example.nativewebview2.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebSettings
import android.webkit.WebView
import com.example.nativewebview2.R
import com.example.nativewebview2.databinding.ActivityMainBinding

class WebModel(
    private val context: Context,
    binding: ActivityMainBinding?,
    private val baseUrl: String,
    private val callback: Callback
) {
    private var webView = binding?.webView

    interface Callback {
        fun onWebViewLoaded()
        fun onWebViewFailed(message: String)
    }

    private val webViewStartTimeout = 30000L

    private var handler: Handler? = null

    private var watchdogTriggered = Runnable {
        Log.d(this.javaClass.simpleName, "Watchdog triggered - Stopping loading and handling error")
        webView?.stopLoading()
        callback.onWebViewFailed("Timeout while loading URL: $baseUrl")
    }

    init {
        WebView.setWebContentsDebuggingEnabled(false)
    }

    private fun startWatchdog() {
        stopWatchdog()

        handler?.let { false } ?: run {
            Log.d(
                this.javaClass.simpleName,
                "Starting watchdog with timeout: $webViewStartTimeout milliseconds"
            )
            handler = Handler(Looper.getMainLooper())
            handler?.postDelayed(watchdogTriggered, webViewStartTimeout)
        }
    }

    private fun stopWatchdog() {
        handler?.let {
            Log.d(this.javaClass.simpleName, "Stopping watchdog")
            it.removeCallbacks(watchdogTriggered)
            handler = null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun startWebView() {
        webView?.let {
            Log.d(
                this.javaClass.simpleName, "<startWebView url: $baseUrl>"
            )
            val webViewClient = WebClient(WebClientCallback())
            val webAppInterface = WebAppInterface()
            webAppInterface.setWebAppInterfaceCallback(WebAppInterfaceCallback())
            it.addJavascriptInterface(
                webAppInterface, context.getString(R.string.view_javascript_interface)
            )
            it.settings.javaScriptEnabled = true
            it.settings.domStorageEnabled = true
            it.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            it.webViewClient = webViewClient
            it.setOnApplyWindowInsetsListener { v, insets ->
                val bottomInset = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> insets.getInsets(WindowInsets.Type.systemBars()).bottom
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        @Suppress("DEPRECATION") insets.systemGestureInsets.bottom
                    }

                    else -> {
                        @Suppress("DEPRECATION") insets.systemWindowInsetBottom
                    }
                }

                v.setPadding(
                    v.paddingLeft, v.paddingTop, v.paddingRight, bottomInset
                )

                insets
            }

            it.loadUrl(baseUrl)

            Log.d(this.javaClass.simpleName, "</startWebView>")

            startWatchdog()
        }
    }

    fun stopWebView() {
        stopWatchdog()

        webView?.let {
            Log.d(this.javaClass.simpleName, "<stopWebView>")
            val parent = it.parent as ViewGroup?
            parent?.removeView(webView)
            it.stopLoading()
            it.removeJavascriptInterface(context.getString(R.string.view_javascript_interface))
            it.destroy()
            Log.d(this.javaClass.simpleName, "</stopWebView>")
            webView = null
        }

    }

    inner class WebClientCallback : WebClient.Callback {
        override fun onPageStarted() {
            stopWatchdog()
        }

        override fun onPageFinished(url: String) {}

        override fun onReceivedError() {
            callback.onWebViewFailed(context.getString(R.string.view_on_received_error_message))
        }
    }

    inner class WebAppInterfaceCallback : WebAppInterface.Callback {
        override fun finishLoading() {
            callback.onWebViewLoaded()
        }
    }
}
