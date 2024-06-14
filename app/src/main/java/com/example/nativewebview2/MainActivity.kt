package com.example.nativewebview2

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import com.example.nativewebview2.viewmodel.ErrorViewModel
import com.example.nativewebview2.viewmodel.WebViewModel
import com.example.nativewebview2.viewmodel.SplashScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundTimeout = 30000L
    private val splashScreenViewModel = SplashScreenViewModel(this, SplashScreenViewModelCallback())
    private val errorViewModel = ErrorViewModel(this)
    private val mainViewModel = WebViewModel(this, MainActivityViewModelCallback())

    private val finishAppTask = Runnable {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            val splashJob = async {
                splashScreenViewModel.start()
            }

            val viewJob = async {
                mainViewModel.start()
            }

            splashJob.await()
            viewJob.await()
        }
    }

    override fun onResume() {
        super.onResume()
        mainHandler.removeCallbacks(finishAppTask)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || mainHandler.hasCallbacks(finishAppTask)) {
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
        mainHandler.postDelayed(this.finishAppTask, backgroundTimeout)
        Log.d(
            this.javaClass.simpleName,
            "onPause : App will finish after $backgroundTimeout milliseconds in the background"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(this.javaClass.simpleName, "Activity is being destroyed")
        splashScreenViewModel.stop()
        mainViewModel.stop()
    }

    inner class SplashScreenViewModelCallback : SplashScreenViewModel.Callback {
        override fun splashScreenFailed(message: String) {
            splashScreenViewModel.stop()
            errorViewModel.showErrorMessage(message)
        }
    }

    inner class MainActivityViewModelCallback : WebViewModel.Callback {
        @MainThread
        override fun onWebViewLoaded() {
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

        override fun onWebViewFailed(message: String) {
            splashScreenViewModel.stop()
            errorViewModel.showErrorMessage(message)
        }
    }
}
