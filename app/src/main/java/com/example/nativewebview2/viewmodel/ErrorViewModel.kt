package com.example.nativewebview2.viewmodel

import android.app.AlertDialog
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

class ErrorViewModel(context: ComponentActivity) : ViewModel() {
    private val contextRef: WeakReference<ComponentActivity> = WeakReference(context)
    @MainThread
    fun showErrorMessage(message: String) {
        contextRef.get()?.let {
            try {
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Ops...")
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    it.finish()
                }
                builder.setCancelable(false)

                val dialog = builder.create()
                dialog.show()
            } catch (e: Exception) {
                Log.e("ErrorViewModel", "showErrorMessage: ", e)
            }
        }
    }
}