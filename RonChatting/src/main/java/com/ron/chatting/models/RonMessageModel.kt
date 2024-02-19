package com.ron.chatting.models

internal data class RonMessageModel(
    val message: String?=null,
    val timeStamp: String?=null,
    val senderId: String?=null,
    val messageId: String?=null,
)
