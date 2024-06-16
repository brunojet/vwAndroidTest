package com.example.nativewebview2.clean.data.domain.models

import com.example.nativewebview2.clean.data.domain.adapters.TriggerAdapter
import com.example.nativewebview2.clean.data.domain.interfaces.TriggerInterface

object Trigger : TriggerInterface {
    private val model = TriggerAdapter.get()
    override fun finish() {
        model.finish()
    }

    override fun webViewLoaded() {
        model.webViewLoaded()
    }

    override fun removeObservers(owner: Any) {
        model.removeObservers(owner)
    }
}