package com.ron.chatting.pushNotificationCalls

import com.ron.chatting.pushNotificationCalls.RetrofitClientFactory.getRetroFitClient
import java.util.Objects

internal class RonApiUtils(private val authKey: String) {
    val restApis: PushNotificationApis?
        get() = Objects.requireNonNull(getRetroFitClient(authKey))?.create(
            PushNotificationApis::class.java
        )
}
