package com.example.nativewebview2.viewmodel

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.databinding.ActivityMainBinding
import com.example.nativewebview2.model.WebModel
import java.lang.ref.WeakReference

class WebViewModel(context: ComponentActivity, private val callback: Callback) : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> = WeakReference(context)
    private var binding: ActivityMainBinding? = null
    private var myWebView: WebModel? = null

    interface Callback {
        fun onWebViewLoaded()
        fun onWebViewFailed(message: String)
    }

    fun start() {
        myWebView?.let { false } ?: run {
            try {
                contextRef.get()?.let {
                    Log.d(this.javaClass.simpleName, "Starting ...")
                    binding = ActivityMainBinding.inflate(it.layoutInflater)

                    myWebView = WebModel(
                        it,
                        binding,
                        it.getString(R.string.base_url),
                        MyWebViewCallback()
                    )

                    myWebView!!.startWebView()
                }
            } catch (e: Exception) {
                callback.onWebViewFailed(e.message ?: "Unknown error during webView start")
            }
        }
    }

    fun stop() {
        myWebView?.let {
            Log.d(this.javaClass.simpleName, "Stopping...")
            it.stopWebView()
            myWebView = null
            binding = null
        }
    }

    fun show() {
        contextRef.get()?.let {
            if (null != binding) {
                it.setContentView(binding!!.root)
            }
        }
    }

    inner class MyWebViewCallback :
        WebModel.Callback {
        override fun onWebViewLoaded() {
            callback.onWebViewLoaded()
        }

        override fun onWebViewFailed(message: String) {
            stop()
            callback.onWebViewFailed(message)
        }
    }
}
