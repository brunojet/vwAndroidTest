package com.example.nativewebview2.clean.business.domain.models

import android.util.Log
import com.example.nativewebview2.clean.data.domain.models.Trigger
import com.example.nativewebview2.clean.data.domain.abstractions.WebAppAbstraction
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Suppress("PropertyName")
@Serializable
data class AppInstall(
    val type: String,
    val name: String,
    val app_id: String,
    val imei: String? = null
)

class BusinessWebApp(private val cb: Callback) : WebAppAbstraction() {

    interface Callback {
        fun postMessage(message: String)
        fun onMessage(message: String)
    }

    override fun postMessage(message: String) {
        super.postMessage(message)

        try {
            val json = Json { ignoreUnknownKeys = true }
            val jsonObject = json.parseToJsonElement(message).jsonObject
            val type = jsonObject["type"]?.jsonPrimitive?.content

            if (null != type) {
                when (type) {
                    "finishLoading" -> Trigger.webViewLoaded()
                    "install" -> install(Json.decodeFromString<AppInstall>(message))
                }
                cb.postMessage("Ready!")
            } else {
                cb.postMessage("Received invalid message payload: $message")
            }
        } catch (e: Exception) {
            cb.postMessage("Received invalid message caused by: ${e.message}")
        }
    }

    override fun onMessage(message: String) {
        super.onMessage(message)
        cb.onMessage("Ready!")
    }

    private fun install(appInstall: AppInstall) {
        Log.d(this.javaClass.simpleName, appInstall.toString())
    }
}
