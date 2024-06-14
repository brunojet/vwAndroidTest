package com.example.nativewebview2.clean.domain.models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

object ActionTriggersDM {
    val finish = MutableLiveData<Boolean>()
    val webViewLoaded = MutableLiveData<Boolean>()

    fun removeObservers(owner: LifecycleOwner) {
        finish.removeObservers(owner)
        webViewLoaded.removeObservers(owner)
    }
}