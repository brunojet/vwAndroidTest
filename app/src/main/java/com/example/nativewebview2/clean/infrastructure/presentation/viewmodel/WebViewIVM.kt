package com.example.nativewebview2.clean.infrastructure.presentation.viewmodel

import android.app.AlertDialog
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.example.nativewebview2.R
import com.example.nativewebview2.databinding.ActivityMainBinding
import com.example.nativewebview2.clean.domain.models.ActionTriggersDM
import com.example.nativewebview2.clean.domain.usecases.ErrorMessageDU
import com.example.nativewebview2.clean.infrastructure.presentation.models.WebViewIM
import java.lang.ref.WeakReference

class WebViewIVM(context: ComponentActivity) : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> by lazy {
        WeakReference(context)
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(context.layoutInflater)
    }
    private var model: WebViewIM? = null

    fun start() {
        model?.let { false } ?: run {
            contextRef.get()?.let {
                try {
                    Log.d(this.javaClass.simpleName, "Starting ...")
                    model = WebViewIM(
                        it, binding, it.getString(R.string.base_url)
                    )
                    model!!.start()
                } catch (e: Exception) {
                    val builder = AlertDialog.Builder(it)
                    ErrorMessageDU.show(
                        builder, ActionTriggersDM.finish, e.message ?: "unknown error"
                    )
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
