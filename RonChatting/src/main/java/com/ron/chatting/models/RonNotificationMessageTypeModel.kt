package com.ron.chatting.models

internal data class RonNotificationMessageTypeModel(
    val message: String,
    val id: String,
    val timestamp: Long = System.currentTimeMillis()
)
