package com.ron.chatting.models

internal data class RonPushNotificationModel(

    var to: String? = null,
    var data: HashMap<String, String>? = null

)
