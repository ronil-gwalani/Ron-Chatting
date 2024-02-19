package com.ron.chatting

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.ron.chatting.activities.RonChatActivity
import com.ron.chatting.callbacks.ChattingResponseCallback
import com.ron.chatting.callbacks.UserLogoutCallback
import com.ron.chatting.callbacks.UserRegisterCallbacks
import com.ron.chatting.helpers.NotificationHelper
import com.ron.chatting.helpers.RonConstants
import com.ron.chatting.helpers.RonSharedPrefUtils
import com.ron.chatting.models.RonChattingUserModel
import com.ron.chatting.models.RonMessageInfoModel
import com.ron.chatting.pushNotificationCalls.PushNotificationChatListener
import java.util.Objects


class RonChattingUtils(private val context: Context) {

    companion object {
        internal var pushNotificationChatListener: PushNotificationChatListener? = null
        internal var openedChatId: String? = null
    }

    private val preferences by lazy {
        RonSharedPrefUtils(context)
    }

    fun requiresChatsNotifications(value: Boolean) {
        preferences.setValue(RonConstants.Preferences.requireNotifications, value)
    }

    fun requiresChatsNotYetStartedText(value: Boolean) {
        preferences.setValue(RonConstants.Preferences.requiresChatsNotYetStartedText, value)
    }

    fun register(
        model: RonChattingUserModel,
        firebaseServerKey: String,
        callback: UserRegisterCallbacks? = null
    ) {
        preferences.setValue(
            RonConstants.Preferences.firebaseServerKeyForNotifications, firebaseServerKey
        )
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseFirestore.getInstance().collection(RonConstants.FirebaseValues.chatting)
                .document(model.userID!!).set(model.apply {
                    fcmToken = it
                }, SetOptions.merge()).addOnSuccessListener {
                    preferences.setUserModel(model)
                    callback?.onUserRegistered(model)
                }.addOnFailureListener {
                    callback?.onUserRegistrationFailed(it.message)
                }

        }


    }

    fun signOut(callback: UserLogoutCallback? = null) {
        val model = preferences.getUserModel()
        model?.let {
            FirebaseFirestore.getInstance().collection(RonConstants.FirebaseValues.chatting)
                .document(it.userID!!).delete().addOnSuccessListener {
                    callback?.onSuccessLogout()
                    preferences.removeKey(RonConstants.Preferences.userModel)
                }.addOnFailureListener {
                    callback?.onLogoutFailed(it.message)
                }
        }
    }

    fun startChatting(
        receiverId: String, uniqueNode: String = "", callback: ChattingResponseCallback? = null
    ) {
        callback?.onProcessStarted()
        FirebaseFirestore.getInstance().collection(RonConstants.FirebaseValues.chatting)
            .document(receiverId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val receiverModel = task.result.toObject(RonChattingUserModel::class.java)
                    val senderModel = preferences.getUserModel()
                    if (receiverModel != null) {
                        callback?.onProcessCompleted()
                        context.startActivity(
                            Intent(context, RonChatActivity::class.java).putExtra(
                                RonConstants.IntentStrings.payload, RonMessageInfoModel(
                                    senderID = senderModel?.userID ?: "",
                                    receiverID = receiverModel.userID ?: "",
                                    senderName = senderModel?.userName ?: "",
                                    receiverName = receiverModel.userName ?: "",
                                    senderFcmId = senderModel?.fcmToken ?: "",
                                    receiverFcmId = receiverModel.fcmToken ?: "",
                                    notificationID = senderModel?.timeStamp.toString(),
                                    senderImage = senderModel?.profileImage ?: "",
                                    receiverImage = receiverModel.profileImage ?: "",
                                    uniqueNodeForTwoSamePersons = uniqueNode,
                                )
                            )
                        )
                    } else {
                        callback?.onErrorFound("User Not Found")
//                        context.showSnackBar("User Not Found")
                    }
                } else {
                    callback?.onErrorFound(task.exception?.message.toString())

                    Log.d("Main", "Error getting documents: ", task.exception)
                }
            }

    }


    fun manageNotifications(data: Map<String?, String?>) {
        if (isFcmChattingPayload(data)) {
            if (preferences.getBooleanValue(RonConstants.Preferences.requireNotifications, true)) {
                if (pushNotificationChatListener != null && openedChatId == data[RonConstants.FirebaseValues.senderID]) {
                    pushNotificationChatListener?.newMessage(
                        context, data
                    )
                } else {
                    NotificationHelper.showNotification(context, data)
                }
            }
        }

    }

    fun isFcmChattingPayload(data: Map<String?, String?>): Boolean {
        if (data.containsKey(RonConstants.FirebaseValues.fcmChattingNotification)) {
            if (Objects.requireNonNull<String?>(data[RonConstants.FirebaseValues.fcmChattingNotification])
                    .equals(
                        RonConstants.FirebaseValues.fcmChattingNotification, ignoreCase = true
                    )
            ) {
                return true
            }
        }
        return false
    }

    fun newTokenGenerated(token: String) {
        RonSharedPrefUtils(context).getUserModel()?.let {
            RonChattingUtils(context).register(
                it.also { model ->
                    model.fcmToken = token
                },
                preferences.getStringValue(RonConstants.Preferences.firebaseServerKeyForNotifications)
            )
        }

    }


}