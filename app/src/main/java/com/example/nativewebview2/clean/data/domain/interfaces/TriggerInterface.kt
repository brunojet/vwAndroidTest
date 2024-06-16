package com.example.nativewebview2.clean.data.domain.interfaces

interface TriggerInterface {

    fun finish()
    fun webViewLoaded()
    fun removeObservers(owner: Any)
}