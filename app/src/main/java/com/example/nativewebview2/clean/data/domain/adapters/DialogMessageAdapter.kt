package com.example.nativewebview2.clean.data.domain.adapters

import com.example.nativewebview2.clean.data.domain.interfaces.DialogMessageInterface

object DialogMessageAdapter {
    private var model: DialogMessageInterface? = null

    fun set(model: DialogMessageInterface) {
        this.model = model
    }

    fun get(): DialogMessageInterface {
        return this.model!!
    }
}