package com.ron.chatting.pushNotificationCalls

import android.content.Context

internal interface PushNotificationChatListener {
    fun newMessage(context: Context?, data: Map<String?, String?>?)
}
