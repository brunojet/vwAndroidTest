package com.example.nativewebview2.mvvm.viewmodel

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.databinding.ActivityMainBinding
import com.example.nativewebview2.clean.infrastructure.presentation.models.AndroidWebViewModel
import java.lang.ref.WeakReference

class WebViewModel(context: ComponentActivity, private val callback: Callback) : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> = WeakReference(context)
    private var binding: ActivityMainBinding? = null
    private var myWebView: AndroidWebViewModel? = null

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

                    myWebView = AndroidWebViewModel(
                        it,
                        binding,
                        it.getString(R.string.base_url)
                    )

                    myWebView!!.start()
                }
            } catch (e: Exception) {
                callback.onWebViewFailed(e.message ?: "Unknown error during webView start")
            }
        }
    }

    fun stop() {
        myWebView?.let {
            Log.d(this.javaClass.simpleName, "Stopping...")
            it.stop()
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
}
