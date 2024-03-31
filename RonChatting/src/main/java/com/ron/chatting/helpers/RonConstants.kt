package com.ron.chatting.helpers

internal interface RonConstants {
    object FirebaseValues {
        const val Users = "Users"
        const val chatting = "RonChattingUtils"
        const val fcmChattingNotification = "fcmChattingNotification"
        const val receiverID = "receiverID"
        const val senderID = "senderID"
        const val uniqueNodeForTwoSamePersons = "uniqueNodeForTwoSamePersons"
        const val receiverName = "receiverName"
        const val senderName = "senderName"
        const val receiverFcmId = "receiverFcmId"
        const val receiverImage = "receiverImage"
        const val senderImage = "senderImage"
        const val notificationID = "notificationID"
        const val notificationMessage = "notificationMessage"
        const val senderFcmId = "senderFcmId"


    }

    object IntentStrings {
        const val payload = "payload"

    }

    object Preferences {
        const val userModel = "userModel"
        const val requireNotifications = "requireNotifications"
        const val requiresChatsNotYetStartedText = "requiresChatsNotYetStartedText"
        const val accessTokenForNotifications = "accessTokenForNotifications"
        const val firebaseProjectId = "firebaseProjectId"

    }

    object Defaults {
        const val chattingNotificationChannel = "Chatting"
        const val chattingNotificationGroupKey = "Chatting Group Key for combing two or more chats"
        const val chattingNotificationChannelDesc = "for two user Chatting"

    }
}