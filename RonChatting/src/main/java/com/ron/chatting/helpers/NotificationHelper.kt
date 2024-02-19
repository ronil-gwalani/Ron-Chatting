package com.ron.chatting.helpers

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.ron.chatting.R
import com.ron.chatting.activities.RonChatActivity
import com.ron.chatting.models.RonMessageInfoModel
import com.ron.chatting.models.RonNotificationMessageTypeModel
import java.util.Locale

internal class NotificationHelper {
    companion object {
        private var notificationManager: NotificationManagerCompat? = null
        private val messageList = ArrayList<RonNotificationMessageTypeModel>()
        private val notificationLists = ArrayList<RonMessageInfoModel>()
        private val userPersonList = HashMap<String, Person>()
        private var notificationID = ""
        fun clearMessageWithID(id: String) {
            messageList.removeIf {
                it.id == id
            }
            notificationLists.removeIf {
                it.receiverID == id
            }
            if (notificationLists.isEmpty()) {
                notificationManager?.cancel(notificationID.toInt())
            }
            userPersonList.remove(id)
        }

        private fun createNotificationChannelIfNeeded(context: Context) {
            if (notificationManager == null) {
                notificationManager = NotificationManagerCompat.from(
                    context
                )
                notificationID = RonSharedPrefUtils(context).getUserModel()?.timeStamp.toString()
            }
            val channel: NotificationChannel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = NotificationChannel(
                    RonConstants.Defaults.chattingNotificationChannel,
                    RonConstants.Defaults.chattingNotificationChannel,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = RonConstants.Defaults.chattingNotificationChannelDesc
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.shouldVibrate()
                channel.shouldShowLights()
                channel.enableLights(true)
                channel.lightColor = Color.GREEN

                channel.canShowBadge()
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes
                )
                notificationManager?.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context, data: Map<String?, String?>) {

            createNotificationChannelIfNeeded(context)
            val messageInfoModel = getInfoModelFromData(context, data)

            val notificationBuilder = NotificationCompat.Builder(
                context, RonConstants.Defaults.chattingNotificationChannel,
            )
            if (!notificationLists.contains(
                    messageInfoModel
                )
            ) {
                notificationLists.add(
                    messageInfoModel
                )
            }
            messageList.add(
                RonNotificationMessageTypeModel(
                    data[RonConstants.FirebaseValues.notificationMessage] ?: "",
                    messageInfoModel.receiverID
                )
            )
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationLists.forEach {
                val notification = getMessagesNotification(
                    context,
                    it,
                    notificationBuilder,
                )

                notificationManager?.notify(
                    it.idForIndividualNotification.toInt(),
                    notification
                )
            }
            setSummaryNotification(context, notificationBuilder)

        }

        private fun setSummaryNotification(
            context: Context,
            notificationBuilder: NotificationCompat.Builder
        ) {

            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = getBasicPendingIntent(context, intent)
            val summaryNotification = notificationBuilder
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSilent(false)
                .setContentTitle("new Message(s)")
                .setContentText("${messageList.size} Message(s) from ${notificationLists.size} Chat(s)")
                .setSmallIcon(R.drawable.ic_chat_notification_icon)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle("${messageList.size} Messages from ${notificationLists.size} Chat(s)")
                        .setSummaryText("new Message(s)")
                )
                .setGroup(RonConstants.Defaults.chattingNotificationGroupKey)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                )
//                .setFullScreenIntent(pendingIntent, true)
                .setOngoing(false)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            summaryNotification.flags = summaryNotification.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager?.notify(
                notificationID.toIntOrNull()
                    ?: System.currentTimeMillis().toInt(), summaryNotification
            )
        }

        private fun getMessagesNotification(
            context: Context,
            messageInfoModel: RonMessageInfoModel,
            notificationBuilder: NotificationCompat.Builder,
        ): Notification {
            val intent = Intent(context, RonChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(
                RonConstants.IntentStrings.payload,
                messageInfoModel
            )
            val pendingIntent = getBasicPendingIntent(context, intent)
            val messageList = getMessagesForId(messageInfoModel.receiverID)

            val user = Person.Builder()
                .setName(messageInfoModel.receiverName)
                .build()

            val notification: Notification =
                notificationBuilder.setContentTitle(messageInfoModel.receiverName)
                    .setSilent(true)
                    .setSmallIcon(R.drawable.ic_chat_notification_icon)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setGroup(RonConstants.Defaults.chattingNotificationGroupKey)
                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.MessagingStyle(
                            userPersonList[messageInfoModel.receiverID] ?: user
                        ).also {
                            messageList.forEach { message ->
                                it.addMessage(
                                    message.message,
                                    message.timestamp,
                                    userPersonList[messageInfoModel.receiverID]
                                )
                            }
                        }
                    )
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false).build()
            notification.flags = Notification.FLAG_INSISTENT
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

            return notification
        }

        private fun getMessagesForId(id: String): ArrayList<RonNotificationMessageTypeModel> {
            val list = ArrayList<RonNotificationMessageTypeModel>()
            messageList.forEach {
                if (it.id == id) {
                    list.add(it)
                }
            }
            return list
        }

        private fun getBasicPendingIntent(context: Context, intent: Intent): PendingIntent {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                PendingIntent.getActivity(
                    context,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getActivity(
                    context,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        }

        private fun getInfoModelFromData(
            context: Context,
            data: Map<String?, String?>
        ): RonMessageInfoModel {
            val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL!!
            val randomColor: Int = colorGenerator.randomColor
            val roundRect: TextDrawable? = TextDrawable.builder().beginConfig()
                ?.width(40)
                ?.height(40)
                ?.endConfig()
                ?.buildRoundRect(
                    (data[RonConstants.FirebaseValues.senderName] ?: "D")[0].toString()
                        .uppercase(Locale.getDefault()),
                    randomColor,
                    200
                )
            val user = Person.Builder()
                .setIcon(
                    IconCompat.createFromIcon(
                        context,
                        Icon.createWithBitmap(roundRect?.toBitmap())
                    )
                )
                .setName(data[RonConstants.FirebaseValues.senderName] ?: "")
                .build()
            userPersonList[data[RonConstants.FirebaseValues.senderID] ?: ""] = user
            return RonMessageInfoModel(
                senderID = data[RonConstants.FirebaseValues.receiverID] ?: "",
                receiverID = data[RonConstants.FirebaseValues.senderID] ?: "",
                uniqueNodeForTwoSamePersons = data[RonConstants.FirebaseValues.uniqueNodeForTwoSamePersons]
                    ?: "",
                senderName = data[RonConstants.FirebaseValues.receiverName] ?: "",
                receiverName = data[RonConstants.FirebaseValues.senderName] ?: "",
                receiverFcmId = data[RonConstants.FirebaseValues.senderFcmId] ?: "",
                senderFcmId = data[RonConstants.FirebaseValues.receiverFcmId] ?: "",
                senderImage = data[RonConstants.FirebaseValues.receiverImage] ?: "",
                receiverImage = data[RonConstants.FirebaseValues.senderImage] ?: "",
                idForIndividualNotification = data[RonConstants.FirebaseValues.notificationID]
                    ?: "",
                notificationID = notificationID,
            )
        }

    }
}