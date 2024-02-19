package com.ron.chatting.models

data class RonChattingUserModel(
    val userID: String? = null,
    val userName: String? = null,
    var fcmToken: String? = null,
    var profileImage: String? = null,
    var timeStamp: Int = System.currentTimeMillis().toString().substring(7).toInt()
)