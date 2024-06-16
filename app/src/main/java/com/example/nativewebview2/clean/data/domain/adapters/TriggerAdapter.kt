package com.example.nativewebview2.clean.data.domain.adapters

import com.example.nativewebview2.clean.data.domain.interfaces.TriggerInterface

object TriggerAdapter {
    private var model: TriggerInterface? = null

    fun set(trigger: TriggerInterface) {
        this.model = trigger
    }

    fun get(): TriggerInterface {
        return this.model!!
    }
}