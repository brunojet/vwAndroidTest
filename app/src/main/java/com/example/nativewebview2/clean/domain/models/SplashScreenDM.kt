package com.example.nativewebview2.clean.domain.models

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nativewebview2.R
import com.example.nativewebview2.clean.domain.usecases.ErrorMessageDU

class SplashScreenDM(
    private val context: ComponentActivity,
    private val text: TextView
) {
    private val handler = Handler(Looper.getMainLooper())
    private var showSplashScreen by mutableStateOf(false)
    private var ssCountUp by mutableIntStateOf(-1)
    private val maxCount = 120

    private val splashRunnable = object : Runnable {
        override fun run() {
            if (showSplashScreen) {
                if (ssCountUp++ < maxCount) {
                    text.text = context.getString(R.string.splash_count, ssCountUp)
                    handler.postDelayed(this, 1000)
                } else {
                    val builder = AlertDialog.Builder(context)
                    ErrorMessageDU.show(
                        builder,
                        ActionTriggersDM.finish,
                        context.getString(R.string.splash_count_final)
                    )
                }
            }
        }
    }

    fun start() {
        if (!showSplashScreen) {
            Log.d(this.javaClass.simpleName, "<start>")
            ssCountUp = 0
            handler.post(splashRunnable)
            Log.d(this.javaClass.simpleName, "</start>")
            showSplashScreen = true
        }
    }

    fun stop() {
        if (showSplashScreen) {
            Log.d(this.javaClass.simpleName, "<stop>")
            handler.removeCallbacks(splashRunnable)
            Log.d(this.javaClass.simpleName, "</stop>")
            showSplashScreen = false
        }
    }
}
