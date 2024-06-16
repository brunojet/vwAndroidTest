package com.example.nativewebview2.clean.infrastructure.presentation.viewmodel

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.clean.infrastructure.domain.providers.AndroidContextProvider
import com.example.nativewebview2.databinding.ActivityMainBinding
import com.example.nativewebview2.clean.infrastructure.presentation.models.AndroidWebViewModel
import com.example.nativewebview2.clean.data.domain.usecases.DialogMessage
import java.lang.ref.WeakReference

class AndroidWebView() : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> by lazy {
        WeakReference(AndroidContextProvider.getContext())
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(AndroidContextProvider.getContext().layoutInflater)
    }
    private var model: AndroidWebViewModel? = null

    fun start() {
        model?.let { false } ?: run {
            contextRef.get()?.let {
                try {
                    Log.d(this.javaClass.simpleName, "Starting ...")
                    model = AndroidWebViewModel(
                        it, binding, it.getString(R.string.base_url)
                    )
                    model!!.start()
                } catch (e: Exception) {
                    DialogMessage.error("Ops...", e.message ?: "unknown error")
                }
            }
        }
    }

    fun stop() {
        model?.let {
            Log.d(this.javaClass.simpleName, "Stopping...")
            it.stop()
            model = null
        }
    }

    fun show() {
        contextRef.get()?.let {
            Log.d(this.javaClass.simpleName, "Showing ...")
            it.setContentView(binding.root)
        }
    }
}
