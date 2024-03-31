package com.ron.chatting.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ron.chatting.RonChattingUtils

class FCMService : FirebaseMessagingService() {
    private val ronChatting by lazy { RonChattingUtils(this) }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ronChatting.newTokenGenerated(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("onMessageReceived", ": ${message.data}")
        if (ronChatting.isFcmChattingPayload(message.data)) {
            ronChatting.manageNotifications(message.data)
            return
        }
    }
}