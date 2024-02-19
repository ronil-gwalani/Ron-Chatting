package com.ron.chatting.callbacks

import com.ron.chatting.models.RonChattingUserModel

interface UserRegisterCallbacks {
    fun onUserRegistered(model : RonChattingUserModel)
    fun onUserRegistrationFailed(error: String?)
}