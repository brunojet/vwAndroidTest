package com.example.nativewebview2.web

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

class MyWebView(
    private val context: Context,
    private val webView: WebView,
    private val baseUrl: String,
    private val webViewCallback: WebViewCallback
) {
    interface WebViewCallback {
        fun onWebViewLoaded()
        fun onLoadError()
        fun onNetworkError()
    }

    private val webViewStartTimeout = 30000L

    private var webViewStarted = true

    private var handler: Handler? = null

    private var watchdogTriggered = Runnable {
        Log.d(this.javaClass.simpleName, "Watchdog triggered - Stopping loading and handling error")
        webView.stopLoading()
        webViewCallback.onLoadError()
    }

    init {
        WebView.setWebContentsDebuggingEnabled(false)
        startWebView()
        startWatchdog()
    }

    private fun startWatchdog() {
        stopWatchdog()
        Log.d(
            this.javaClass.simpleName,
            "Starting watchdog with timeout: $webViewStartTimeout milliseconds"
        )
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(watchdogTriggered, webViewStartTimeout)
    }

    private fun stopWatchdog() {
        if (handler != null) {
            Log.d(this.javaClass.simpleName, "Stopping watchdog")
            handler?.removeCallbacks(watchdogTriggered)
            handler = null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView() {
        Log.d(
            this.javaClass.simpleName, "<startWebView url: $baseUrl>"
        )
        val webViewClient = MyWebViewClient()
        val webAppInterface = MyWebAppInterface()
        webViewClient.setWebViewCallback(MyWebViewClientCallback())
        webAppInterface.setWebAppInterfaceCallback(MyWebAppInterfaceCallback())
        webView.addJavascriptInterface(
            webAppInterface, context.getString(R.string.view_javascript_interface)
        )
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.webViewClient = webViewClient
        webView.setOnApplyWindowInsetsListener { v, insets ->
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

        webView.loadUrl(baseUrl)

        Log.d(this.javaClass.simpleName, "</startWebView>")
    }

    fun stopWebView() {
        if (webViewStarted) {
            Log.d(this.javaClass.simpleName, "<stopWebView $webView.visibility>")
            val parent = webView.parent as ViewGroup?
            parent?.removeView(webView)
            webView.removeJavascriptInterface(context.getString(R.string.view_javascript_interface))
            webView.destroy()
            Log.d(this.javaClass.simpleName, "</stopWebView>")
            webViewStarted = false
        }
    }

    inner class MyWebViewClientCallback : MyWebViewClient.WebViewClientCallback {
        override fun onPageStarted() {
            stopWatchdog()
        }

        override fun onPageFinished(url: String) {}

        override fun onReceivedError() {
            stopWatchdog()
            webViewCallback.onNetworkError()
        }
    }

    inner class MyWebAppInterfaceCallback : MyWebAppInterface.WebAppInterfaceCallback {
        override fun finishLoading() {
            webViewCallback.onWebViewLoaded()
        }
    }
}
