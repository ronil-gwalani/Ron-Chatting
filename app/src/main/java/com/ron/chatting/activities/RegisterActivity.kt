package com.ron.chatting.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.ron.chatting.RonChattingUtils
import com.ron.chatting.callbacks.UserRegisterCallbacks
import com.ron.chatting.databinding.ActivityRegisterBinding
import com.ron.chatting.databinding.ProgressDialogueBinding
import com.ron.chatting.models.RonChattingUserModel


class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val progressBar by lazy {
        AlertDialog.Builder(this).setView(ProgressDialogueBinding.inflate(layoutInflater).root)
            .create().also {
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.register.setOnClickListener {
            if (binding.etTargetID.text.toString().length < 5) {
                Toast.makeText(
                    this,
                    "UserID must be valid(length greater then 5)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (binding.etUserName.text.toString().length < 5) {
                Toast.makeText(
                    this,
                    "etUserName must be valid(length greater then 5)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            registerUser(binding.etTargetID.text.toString(), binding.etUserName.text.toString())

        }
    }

    private fun registerUser(userId: String, userName: String) {
        progressBar.show()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val ronChattingUtils = RonChattingUtils(this)
            ronChattingUtils.requiresChatsNotYetStartedText(false)
            ronChattingUtils.register(RonChattingUserModel(
                userID = userId,
                userName = userName,
                fcmToken = it,
                profileImage = "https://firebasestorage.googleapis.com/v0/b/ronil-firebase.appspot.com/o/Profile%2Fimage1.jpeg?alt=media&token=9d780ca4-c808-438b-98cd-83cff5d9b261"//Can be null
            ),
                object : UserRegisterCallbacks {
                    override fun onUserRegistered(model: RonChattingUserModel) {
                        progressBar.dismiss()
                        val sp =
                            getSharedPreferences(this@RegisterActivity.packageName, MODE_PRIVATE)
                        sp.edit().putString("userId", userId).apply()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onUserRegistrationFailed(error: String?) {
                        progressBar.dismiss()
                    }
                })
        }

    }
}