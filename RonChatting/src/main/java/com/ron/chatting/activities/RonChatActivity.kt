package com.ron.chatting.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.ron.chatting.R
import com.ron.chatting.RonChattingUtils
import com.ron.chatting.adapters.RonChatsAdapter
import com.ron.chatting.databinding.ActivityRonChatBinding
import com.ron.chatting.helpers.NotificationHelper
import com.ron.chatting.helpers.RonConstants
import com.ron.chatting.helpers.RonSharedPrefUtils
import com.ron.chatting.models.RonMessageInfoModel
import com.ron.chatting.models.RonMessageModel
import com.ron.chatting.models.RonPushNotificationModel
import com.ron.chatting.pushNotificationCalls.PushNotificationChatListener
import com.ron.chatting.pushNotificationCalls.RonApiUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class RonChatActivity : AppCompatActivity() {
    private val preferences by lazy { RonSharedPrefUtils(this) }
    private val restApis by lazy {
        RonApiUtils(preferences.getStringValue(RonConstants.Preferences.firebaseServerKeyForNotifications)).restApis
    }
    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val binding by lazy { ActivityRonChatBinding.inflate(layoutInflater) }
    private val messageInfoModel by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                RonConstants.IntentStrings.payload,
                RonMessageInfoModel::class.java
            )!!
        } else {
            intent.getSerializableExtra(
                RonConstants.IntentStrings.payload,
            ) as RonMessageInfoModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val messages: ArrayList<RonMessageModel> = ArrayList()
        binding.name.text = messageInfoModel.receiverName
        if (!messageInfoModel.receiverImage.isNullOrEmpty()) {
            Glide.with(binding.imgProfile).load(messageInfoModel.receiverImage)
                .placeholder(R.drawable.ron_user).into(binding.imgProfile)
        }
        val adapter = RonChatsAdapter(messages, messageInfoModel.senderID)
        binding.messagesRecycler.adapter = adapter
        binding.imgBack.setOnClickListener { finishAndRemoveTask() }
        databaseReference.child(RonConstants.FirebaseValues.chatting)
            .child(messageInfoModel.senderID + messageInfoModel.uniqueNodeForTwoSamePersons + messageInfoModel.receiverID)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    if (binding.progressBar.visibility == View.VISIBLE) {
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    if (preferences.getBooleanValue(
                            RonConstants.Preferences.requireNotifications,
                            true
                        )
                    ) {
                        binding.noMessagesText.visibility = View.VISIBLE
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        databaseReference.child(RonConstants.FirebaseValues.chatting)
            .child(messageInfoModel.senderID + messageInfoModel.uniqueNodeForTwoSamePersons + messageInfoModel.receiverID)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (binding.progressBar.visibility == View.VISIBLE) {
                        binding.progressBar.visibility = View.GONE
                    }
                    if (binding.noMessagesText.visibility == View.VISIBLE) {
                        binding.noMessagesText.visibility = View.GONE
                    }
                    val model: RonMessageModel? = snapshot.getValue(RonMessageModel::class.java)
                    if (model != null) {
                        adapter.addMessage(model)
                        binding.messagesRecycler.smoothScrollToPosition(messages.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        binding.send.setOnClickListener {
            if (binding.message.text.toString().trim().isNotEmpty()) {
                binding.send.isEnabled = false
                val timeStamp =
                    (System.currentTimeMillis() / 1000).toString()
                val message = RonMessageModel(
                    binding.message.text.toString().trim(),
                    timeStamp,
                    messageInfoModel.senderID,
                    timeStamp
                )
                binding.message.setText("")
                databaseReference.child(RonConstants.FirebaseValues.chatting)
                    .child(messageInfoModel.senderID + messageInfoModel.uniqueNodeForTwoSamePersons + messageInfoModel.receiverID)
                    .child(timeStamp)
                    .setValue(message)
                    .addOnSuccessListener {
                        databaseReference.child(RonConstants.FirebaseValues.chatting)
                            .child(messageInfoModel.receiverID + messageInfoModel.uniqueNodeForTwoSamePersons + messageInfoModel.senderID)
                            .child(timeStamp)
                            .setValue(message)
                            .addOnSuccessListener {
                                binding.message.setText("")
                                binding.send.isEnabled = true
                                sendNotification(
                                    message,
                                )
                            }
                    }
            } else {
                binding.message.setText("")
            }
        }
    }

    private fun sendNotification(
        message: RonMessageModel,

        ) {
        val hashMap = HashMap<String, String>()
        hashMap[RonConstants.FirebaseValues.fcmChattingNotification] =
            RonConstants.FirebaseValues.fcmChattingNotification
        hashMap[RonConstants.FirebaseValues.senderID] = messageInfoModel.senderID
        hashMap[RonConstants.FirebaseValues.receiverID] = messageInfoModel.receiverID
        hashMap[RonConstants.FirebaseValues.uniqueNodeForTwoSamePersons] =
            messageInfoModel.uniqueNodeForTwoSamePersons
        hashMap[RonConstants.FirebaseValues.receiverName] = messageInfoModel.receiverName
        hashMap[RonConstants.FirebaseValues.senderName] = messageInfoModel.senderName
        hashMap[RonConstants.FirebaseValues.senderFcmId] = messageInfoModel.senderFcmId
        hashMap[RonConstants.FirebaseValues.receiverFcmId] = messageInfoModel.receiverFcmId
        hashMap[RonConstants.FirebaseValues.notificationID] = messageInfoModel.notificationID
        hashMap[RonConstants.FirebaseValues.senderImage] = messageInfoModel.senderImage ?: ""
        hashMap[RonConstants.FirebaseValues.receiverImage] = messageInfoModel.receiverImage ?: ""
        hashMap[RonConstants.FirebaseValues.notificationMessage] = message.message ?: ""
        restApis?.sendMessage(RonPushNotificationModel(messageInfoModel.receiverFcmId, hashMap))
            ?.enqueue(object : Callback<Response<ResponseBody?>?> {
                override fun onResponse(
                    call: Call<Response<ResponseBody?>?>,
                    response: Response<Response<ResponseBody?>?>
                ) {
                    Log.d("onApiSuccess", ": " + response.isSuccessful)
                }

                override fun onFailure(call: Call<Response<ResponseBody?>?>, t: Throwable) {
                    Log.d("onFailure", ": " + t.message)
                }
            })

    }

    override fun onResume() {
        try {
            NotificationHelper.clearMessageWithID(messageInfoModel.receiverID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        RonChattingUtils.pushNotificationChatListener = messageListener
        RonChattingUtils.openedChatId = messageInfoModel.receiverID
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        RonChattingUtils.pushNotificationChatListener = null
        RonChattingUtils.openedChatId = null

    }

    private val messageListener = object : PushNotificationChatListener {
        override fun newMessage(context: Context?, data: Map<String?, String?>?) {

        }

    }
}




