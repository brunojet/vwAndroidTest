package com.example.nativewebview2.clean.infrastructure.presentation.models

import android.graphics.Bitmap
import android.util.Log
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class AndroidWebClientModel(private val callback: Callback) : WebViewClient() {
    interface Callback {
        fun onPageStarted()
        fun onPageFinished(url: String)
        fun onReceivedError()
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d(this.javaClass.simpleName, "onPageStarted: $url")
        callback.onPageStarted()
    }

    override fun onReceivedHttpError(
        view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        Log.d(
            this.javaClass.simpleName,
            "onReceivedHttpError: ${request?.url} Error: ${errorResponse?.reasonPhrase}"
        )
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        super.onScaleChanged(view, oldScale, newScale)
        Log.d(this.javaClass.simpleName, "onScaleChanged: $newScale")
    }

    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        Log.d(this.javaClass.simpleName, "onRenderProcessGone: ${detail ?: ""} ")
        return super.onRenderProcessGone(view, detail)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(this.javaClass.simpleName, "onPageFinished: $url")
        callback.onPageFinished(url ?: "")
    }

    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        Log.d(
            this.javaClass.simpleName,
            "onReceivedError: ${request?.url} Error: ${error?.description}/${error}"
        )
        val errorCode = error?.errorCode ?: -1

        when (errorCode) {
            ERROR_HOST_LOOKUP -> {
                callback.onReceivedError()
            }

            else -> {
                Log.e(
                    this.javaClass.simpleName, "onReceivedError: ${error?.description}($errorCode)"
                )
            }
        }

    }
}
