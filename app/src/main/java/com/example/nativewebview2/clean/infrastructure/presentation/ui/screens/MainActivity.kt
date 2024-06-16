package com.example.nativewebview2.clean.infrastructure.presentation.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.ComponentActivity
import com.example.nativewebview2.R
import com.example.nativewebview2.clean.infrastructure.domain.models.AndroidTrigger
import com.example.nativewebview2.clean.infrastructure.domain.providers.AndroidContextProvider
import com.example.nativewebview2.clean.infrastructure.domain.usecases.AndroidDialogMessage
import com.example.nativewebview2.clean.infrastructure.presentation.viewmodel.AndroidSplashScreenViewModel
import com.example.nativewebview2.clean.infrastructure.presentation.viewmodel.AndroidWebView
import com.example.nativewebview2.clean.data.domain.adapters.DialogMessageAdapter
import com.example.nativewebview2.clean.data.domain.adapters.TriggerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val splashScreenViewModel = AndroidSplashScreenViewModel()
    private val mainViewModel = AndroidWebView()
    private val backgroundTimeout = 30000L
    private val finishRunnable = Runnable { finish() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "onCreate event")

        AndroidContextProvider.setContext(this)
        DialogMessageAdapter.set(AndroidDialogMessage)
        TriggerAdapter.set(AndroidTrigger)

        CoroutineScope(Dispatchers.Main).launch {
            val splashJob = async {
                //splashScreenViewModel.start()
            }

            val viewJob = async {
                mainViewModel.show()
                mainViewModel.start()
            }

            splashJob.await()
            viewJob.await()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(this.javaClass.simpleName, "onResume event")
        handler.removeCallbacks(finishRunnable)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || handler.hasCallbacks(finishRunnable)) {
            Log.d(this.javaClass.simpleName, "onResume: App background timeout canceled")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(this.javaClass.simpleName, "onStart event")

        AndroidTrigger.webViewLoaded.observe(this) {
            Log.d(this.javaClass.simpleName, "Received webViewLoaded trigger")
            if (it) {
                Log.d(this.javaClass.simpleName, "Executing webViewLoaded trigger")
                performScreenTransitionAnimation()
            }
        }

        AndroidTrigger.finish.observe(this) {
            Log.d(this.javaClass.simpleName, "Received finish trigger")
            if (it) {
                Log.d(this.javaClass.simpleName, "Executing finish trigger")
                finish()
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Deprecated(
        "This method is deprecated, but overridden for custom behavior.",
        ReplaceWith("moveTaskToBack(true)")
    )
    override fun onBackPressed() {
        Log.d(this.javaClass.simpleName, "onBackPressed event")
        moveTaskToBack(true)
        handler.postDelayed(finishRunnable, backgroundTimeout)
        Log.d(
            this.javaClass.simpleName,
            "onPause : App will finish after $backgroundTimeout milliseconds in the background"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName, "onDestroy event")
        AndroidTrigger.removeObservers(this)
        splashScreenViewModel.stop()
        mainViewModel.stop()
    }

    private fun performScreenTransitionAnimation() {
        val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)

        splashScreenViewModel.getView()?.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                splashScreenViewModel.stop()
            }

            override fun onAnimationEnd(animation: Animation) {
                mainViewModel.show()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
