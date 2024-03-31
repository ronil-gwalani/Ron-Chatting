package com.ron.chatting.pushNotificationCalls

import java.util.Objects

internal class RonApiUtils(private var headerAuthKey: String) {
    val restApis: PushNotificationApis?
        get() = Objects.requireNonNull(RetrofitClientFactory().getRetroFitClient(headerAuthKey))?.create(
            PushNotificationApis::class.java
        )
}
