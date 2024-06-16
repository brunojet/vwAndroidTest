package com.example.nativewebview2.clean.data.domain.usecases

import com.example.nativewebview2.clean.data.domain.adapters.DialogMessageAdapter
import com.example.nativewebview2.clean.data.domain.interfaces.DialogMessageInterface

object DialogMessage : DialogMessageInterface {
    private val model = DialogMessageAdapter.get()

    override fun error(title: String, message: String) {
        model.error(title, message)
    }
}