package com.example.nativewebview2.clean.infrastructure.domain.providers

import androidx.activity.ComponentActivity

object AndroidContextProvider {
    private var context: ComponentActivity? = null

    fun setContext(context: Any) {
        AndroidContextProvider.context = context as ComponentActivity
    }

    fun getContext(): ComponentActivity {
        return context
            ?: throw IllegalStateException("Context not set in AndroidContextProvider")
    }
}