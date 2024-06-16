package com.example.nativewebview2.clean.infrastructure.domain.models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.nativewebview2.clean.data.domain.interfaces.TriggerInterface

object AndroidTrigger : TriggerInterface {
    val finish = MutableLiveData<Boolean>()
    val webViewLoaded = MutableLiveData<Boolean>()
    override fun finish() {
        finish.postValue(true)
    }

    override fun webViewLoaded() {
        webViewLoaded.postValue(true)
    }


    override fun removeObservers(owner: Any) {
        val androidOwner = owner as LifecycleOwner
        finish.removeObservers(androidOwner)
        webViewLoaded.removeObservers(androidOwner)
    }
}