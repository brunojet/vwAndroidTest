package com.example.nativewebview2.clean.infrastructure.presentation.viewmodel

import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.clean.domain.models.SplashScreenDM
import java.lang.ref.WeakReference

class SplashScreenIVM(context: ComponentActivity) : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> = WeakReference(context)
    private var model: SplashScreenDM? = null

    fun start() {
        contextRef.get()?.let {
            it.setContentView(R.layout.activity_splash)
            val ssCountUpText = it.findViewById<TextView>(R.id.splash_count_up_text)
            model = SplashScreenDM(it, ssCountUpText)
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