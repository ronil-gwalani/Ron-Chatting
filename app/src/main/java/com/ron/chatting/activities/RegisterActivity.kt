package com.ron.chatting.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
//import com.ron.chatting.RonChattingUtils
//import com.ron.chatting.callbacks.UserRegisterCallbacks
//import com.ron.chatting.models.RonChattingUserModel
import com.ron.chatting.databinding.ActivityRegisterBinding
import com.ron.chatting.databinding.ProgressDialogueBinding


class RegisterActivity : AppCompatActivity() {
    private val authKey =
        "AAAAy2O4kR0:APA91bE_LqTu8mt-S0ZNpaQuMigzHfz4l9rsGiyxe-EUFZtJuGIpSTvXID8m9qlUy514OlM0rFB-vT8kl8YiSqblDvVpjoLai6rTD0kkc8ODVtVe4dK0FZQI-bOb0gEMO_2N7H0tyO0T"
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
//            RonChattingUtils(this).register(RonChattingUserModel(
//                userID = userId,
//                userName = userName,
//                fcmToken = it,
//                profileImage = "https://i0.wp.com/www.smartprix.com/bytes/wp-content/uploads/2023/05/2-photoutils.com_.jpg?ssl=1&quality=80&w=f"//Can be null
//            ), authKey,
//                object : UserRegisterCallbacks {
//                    override fun onUserRegistered(model: RonChattingUserModel) {
//                        progressBar.dismiss()
//                        val sp =
//                            getSharedPreferences(this@RegisterActivity.packageName, MODE_PRIVATE)
//                        sp.edit().putString("userId", userId).apply()
//                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
//                        finish()
//                    }
//
//                    override fun onUserRegistrationFailed(error: String?) {
//                        progressBar.dismiss()
//                    }
//                })
        }

    }
}