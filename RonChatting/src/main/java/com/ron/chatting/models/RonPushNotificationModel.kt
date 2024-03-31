package com.ron.chatting.models

internal data class RonPushNotificationModel(

    var to: String? = null,
    var data: HashMap<String, String>? = null

)

internal data class RonPushNewNotificationModel(
    var message: RonPushNewNotificationMessageModel? = null,
)

internal data class RonPushNewNotificationMessageModel(
    var token: String? = null,
    var data: HashMap<String, String>? = null,
    var android: AndroidPriorityModel = AndroidPriorityModel()
)

internal data class AndroidPriorityModel(
    var priority: String = "high",
)