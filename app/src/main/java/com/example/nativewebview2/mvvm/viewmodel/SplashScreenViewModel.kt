package com.example.nativewebview2.mvvm.viewmodel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.mvvm.model.SplashScreenModel
import java.lang.ref.WeakReference

class SplashScreenViewModel(context: ComponentActivity, private val callback: Callback) :
    ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> = WeakReference(context)
    private var ssHandler: SplashScreenModel? = null

    interface Callback {
        fun splashScreenFailed(message: String)
    }

    fun start() {
        ssHandler?.let { false } ?: run {
            contextRef.get()?.let {
                Log.d(this.javaClass.simpleName, "Starting...")
                it.setContentView(R.layout.activity_splash)
                val ssCountUpText = it.findViewById<TextView>(R.id.splash_count_up_text)
                ssHandler = SplashScreenModel(it, SplashScreenModelCallback(), ssCountUpText)
                ssHandler!!.startSplashScreen()
            }
        }
    }

    fun stop() {
        ssHandler?.let {
            Log.d(this.javaClass.simpleName, "Stopping...")
            it.stopSplashScreen()
            ssHandler = null
        }
    }

    fun getView(): View? {
        ssHandler?.let {
            contextRef.get()?.let {
                return it.findViewById(R.id.activity_splash)
            }
        }

        return null
    }

    inner class SplashScreenModelCallback : SplashScreenModel.Callback {
        override fun splashScreenFailed(message: String) {
            stop()
            callback.splashScreenFailed(message)
        }
    }

}