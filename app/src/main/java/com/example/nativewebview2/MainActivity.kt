package com.example.nativewebview2

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.nativewebview2.splashscreen.SplashScreenHandler
import com.example.nativewebview2.databinding.ActivityMainBinding
import com.example.nativewebview2.helper.MyBroadcastReceiver
import com.example.nativewebview2.web.MyWebView

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myReceiver: MyBroadcastReceiver
    private var ssHandler: SplashScreenHandler? = null
    private var webView: MyWebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundTimeout = 30000L

    private fun loadWebView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        webView = MyWebView(
            this, binding.webView, getString(R.string.base_url), MyWebViewCallback()
        )
    }

    private fun startSplashScreen() {
        setContentView(R.layout.activity_splash)
        val ssCountUpText = findViewById<TextView>(R.id.splash_count_up_text)
        ssHandler = SplashScreenHandler(this, ssCountUpText)
        ssHandler!!.startSplashScreen()
    }

    private val finishAppTask = Runnable {
        finish()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadWebView()
        startSplashScreen()
        myReceiver = MyBroadcastReceiver(this) {
            finish()
        }
        myReceiver.register(getString(R.string.exit_app_action))
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || mainHandler.hasCallbacks(finishAppTask)) {
            mainHandler.removeCallbacks(finishAppTask)
            Log.d(this.javaClass.simpleName, "onResume: App background timeout canceled")
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated(
        "This method is deprecated, but overridden for custom behavior.",
        ReplaceWith("moveTaskToBack(true)")
    )
    override fun onBackPressed() {
        moveTaskToBack(true)
        mainHandler.postDelayed(finishAppTask, backgroundTimeout)
        Log.d(
            this.javaClass.simpleName,
            "onPause : App will finish after $backgroundTimeout milliseconds in the background"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName, "Activity is being destroyed")
        myReceiver.unregister()
        ssHandler?.stopSplashScreen()
        webView?.stopWebView()
    }

    inner class MyWebViewCallback : MyWebView.WebViewCallback {
        override fun onWebViewLoaded() {
            mainHandler.post {
                val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                findViewById<View>(R.id.activity_splash).startAnimation(fadeOut)

                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        ssHandler?.stopSplashScreen()
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        setContentView(binding.root)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
        }

        override fun onLoadError() {
            ssHandler?.stopApplication(getString(R.string.view_on_received_error_message))
        }

        override fun onNetworkError() {
            ssHandler?.stopApplication(getString(R.string.view_on_received_error_message))
        }
    }
}
