package com.example.nativewebview2.clean.domain.usecases

import android.app.AlertDialog
import android.util.Log
import androidx.lifecycle.MutableLiveData

object ErrorMessageDU {
    fun show(
        builder: AlertDialog.Builder,
        trigger: MutableLiveData<Boolean>?,
        message: String
    ) {
        try {
            builder.setTitle("Ops...")
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                trigger?.postValue(true)
            }
            builder.setCancelable(false)

            val dialog = builder.create()
            dialog.show()
        } catch (e: Exception) {
            Log.e("ErrorMessageDialog", "show: ", e)
        }
    }
}
