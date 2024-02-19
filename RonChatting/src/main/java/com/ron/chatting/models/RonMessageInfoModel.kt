package com.ron.chatting.models

import java.io.Serializable

internal data class RonMessageInfoModel(
    val senderID: String,
    val receiverID: String,
    val receiverFcmId: String,
    val senderFcmId: String,
    val senderName: String,
    val receiverName: String,
    val notificationID: String,
    val senderImage: String? = null,
    val receiverImage: String? = null,
    val uniqueNodeForTwoSamePersons: String = "",
    val idForIndividualNotification: String = "",
) : Serializable