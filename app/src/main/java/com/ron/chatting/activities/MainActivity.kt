package com.ron.chatting.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
//import com.ron.chatting.RonChattingUtils
//import com.ron.chatting.callbacks.ChattingResponseCallback
//import com.ron.chatting.callbacks.UserLogoutCallback
import com.ron.chatting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var sp: SharedPreferences? = null

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("onCreate", ": ${System.currentTimeMillis().toString().substring(7)}")
        sp = getSharedPreferences(this.packageName, MODE_PRIVATE)
        val userID: String? = sp!!.getString("userId", "")
        binding.yourUserId.text = "Your User ID :$userID"
        binding.btnChat.setOnClickListener {
//            RonChattingUtils(this).startChatting(binding.etTargetID.text.toString(), callback = object:ChattingResponseCallback{
//                override fun onProcessStarted() {
//                    Log.d("onProcessStarted", ": ", )
//                }
//
//                override fun onErrorFound(error: String?) {
//                    Log.d("onErrorFound", ": ", )
//                }
//
//                override fun onProcessCompleted() {
//                    Log.d("onProcessCompleted", ": ", )
//                }
//            })
        }
        binding.logout.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Alert").setMessage("Are u Sure u wanna Logout")
                .setPositiveButton("Yes") { _, _ ->
                    logoutFromServer()
                }.setNegativeButton("Cancel") { _, _ ->
                }.show()
        }
    }

    private fun logoutFromServer() {
//        RonChattingUtils(this).signOut(object : UserLogoutCallback {
//            override fun onSuccessLogout() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "User Logged out Successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//                sp?.edit()?.clear()?.apply()
//                startActivity(Intent(this@MainActivity, SplashActivity::class.java))
//                finishAffinity()
//            }
//
//            override fun onLogoutFailed(error: String?) {
//                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
//                Log.e("onPushTokenUnregistered", ":ERROR ->  $error")
//            }
//        })
    }

}