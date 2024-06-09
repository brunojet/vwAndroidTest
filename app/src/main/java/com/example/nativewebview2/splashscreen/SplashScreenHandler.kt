package com.example.nativewebview2.splashscreen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nativewebview2.R

class SplashScreenHandler(private val context: Context, private val text: TextView) {
    private val handler = Handler(Looper.getMainLooper())
    private var showSplashScreen by mutableStateOf(false)
    private var ssCountUp by mutableIntStateOf(-1)
    private val maxCount = 120

    private val showErrorDialogRunnable = { message: String ->
        Runnable {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error")
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(context.getString(R.string.exit_app_action))
                intent.putExtra("EXIT", true)
                context.sendBroadcast(intent)
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }

    private val splashRunnable = object : Runnable {
        override fun run() {
            if (showSplashScreen) {
                if (ssCountUp++ < maxCount) {
                    text.text = context.getString(R.string.splash_count, ssCountUp)
                    handler.postDelayed(this, 1000)
                } else {
                    handler.post(showErrorDialogRunnable("Count exceeded the maximum limit."))
                }
            }
        }
    }

    fun startSplashScreen() {
        Log.d(this.javaClass.simpleName, "<startSplashScreen>")
        showSplashScreen = true
        ssCountUp = 0
        handler.post(splashRunnable)
        Log.d(this.javaClass.simpleName, "</startSplashScreen>")
    }

    fun stopSplashScreen(): Boolean {
        if (showSplashScreen) {
            Log.d(this.javaClass.simpleName, "<stopSplashScreen>")
            handler.removeCallbacks(splashRunnable)
            showSplashScreen = false
            Log.d(this.javaClass.simpleName, "</stopSplashScreen>")
            return true
        }

        return false
    }

    fun stopApplication(message: String) {
        if (stopSplashScreen()) {
            handler.post(showErrorDialogRunnable(message))
        }
    }
}
