package com.example.nativewebview2.mvvm.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nativewebview2.R

class SplashScreenModel(
    private val context: Context, private val callback: Callback, private val text: TextView
) {
    private val handler = Handler(Looper.getMainLooper())
    private var showSplashScreen by mutableStateOf(false)
    private var ssCountUp by mutableIntStateOf(-1)
    private val maxCount = 120

    interface Callback {
        fun splashScreenFailed(message: String)
    }

    private val splashRunnable = object : Runnable {
        override fun run() {
            if (showSplashScreen) {
                if (ssCountUp++ < maxCount) {
                    text.text = context.getString(R.string.splash_count, ssCountUp)
                    handler.postDelayed(this, 1000)
                } else {
                    callback.splashScreenFailed(context.getString(R.string.splash_count_final))
                }
            }
        }
    }

    fun startSplashScreen() {
        if (!showSplashScreen) {
            Log.d(this.javaClass.simpleName, "<startSplashScreen>")
            ssCountUp = 0
            handler.post(splashRunnable)
            Log.d(this.javaClass.simpleName, "</startSplashScreen>")
            showSplashScreen = true
        }
    }

    fun stopSplashScreen() {
        if (showSplashScreen) {
            Log.d(this.javaClass.simpleName, "<stopSplashScreen>")
            handler.removeCallbacks(splashRunnable)
            Log.d(this.javaClass.simpleName, "</stopSplashScreen>")
            showSplashScreen = false
        }
    }
}
