package com.example.nativewebview2.clean.infrastructure.presentation.viewmodel

import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.clean.infrastructure.domain.providers.AndroidContextProvider
import com.example.nativewebview2.clean.infrastructure.presentation.models.AndroidSplashScreenModel
import java.lang.ref.WeakReference

class AndroidSplashScreenViewModel() : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> by lazy {
        WeakReference(AndroidContextProvider.getContext())
    }
    private var model: AndroidSplashScreenModel? = null

    fun start() {
        contextRef.get()?.let {
            it.setContentView(R.layout.activity_splash)
            val ssCountUpText = it.findViewById<TextView>(R.id.splash_count_up_text)
            model = AndroidSplashScreenModel(it, ssCountUpText)
            model?.start()
        }
    }

    fun stop() {
        model?.stop()
        model = null
    }

    fun getView(): View? {
        contextRef.get()?.let {
            return it.findViewById(R.id.activity_splash)
        }

        return null
    }
}