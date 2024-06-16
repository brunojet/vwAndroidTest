package com.example.nativewebview2.clean.infrastructure.domain.usecases

import android.app.AlertDialog
import android.util.Log
import androidx.annotation.MainThread
import com.example.nativewebview2.clean.infrastructure.domain.models.AndroidTrigger
import com.example.nativewebview2.clean.infrastructure.domain.providers.AndroidContextProvider
import com.example.nativewebview2.clean.data.domain.interfaces.DialogMessageInterface

object AndroidDialogMessage: DialogMessageInterface {
    override fun error(
        title: String,
        message: String
    ) {
        try {
            val context = AndroidContextProvider.getContext()
            context?.runOnUiThread {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(title)
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    AndroidTrigger.finish.postValue(true)
                }
                builder.setCancelable(false)

                val dialog = builder.create()
                dialog.show()
            }
        } catch (e: Exception) {
            Log.e("ErrorMessageDialog", "show: ", e)
        }
    }
}

