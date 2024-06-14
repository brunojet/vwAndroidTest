package com.example.nativewebview2.clean.infrastructure.presentation.models

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.nativewebview2.clean.domain.models.ActionTriggersDM
import com.example.nativewebview2.clean.domain.usecases.ErrorMessageDU

class WebViewIM(
    private val context: Context,
    binding: ActivityMainBinding?,
    private val baseUrl: String
) {
    private var webView = binding?.webView

    private val webViewStartTimeout = 30000L

    private var handler: Handler? = null

    private var watchdogTriggered = Runnable {
        Log.d(this.javaClass.simpleName, "Watchdog triggered - Stopping loading and handling error")
        showErrorMessageAndQuit(context.getString(R.string.view_loading_timeout))
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

    private fun showErrorMessageAndQuit(message: String) {
        stop()
        val builder = AlertDialog.Builder(context)
        ErrorMessageDU.show(builder, ActionTriggersDM.finish, message)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun start() {
        webView?.let {
            val webViewClient = WebClientIM(WebClientCallback())
            it.addJavascriptInterface(
                WebAppItfIM(), context.getString(R.string.view_javascript_interface)
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

            Log.d(this.javaClass.simpleName, "</start>")

            startWatchdog()
        }
    }

    fun stop() {
        stopWatchdog()

        webView?.let {
            Log.d(this.javaClass.simpleName, "<stop>")
            val parent = it.parent as ViewGroup?
            parent?.removeView(webView)
            it.stopLoading()
            it.removeJavascriptInterface(context.getString(R.string.view_javascript_interface))
            it.destroy()
            Log.d(this.javaClass.simpleName, "</stop>")
            webView = null
        }
    }

    inner class WebClientCallback : WebClientIM.Callback {
        override fun onPageStarted() {
            stopWatchdog()
        }

        override fun onPageFinished(url: String) {}

        override fun onReceivedError() {
            showErrorMessageAndQuit(context.getString(R.string.view_on_received_error_message))
        }
    }
}
