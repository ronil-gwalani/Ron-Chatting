package com.ron.chatting.callbacks

interface UserLogoutCallback {
    fun onSuccessLogout()
    fun onLogoutFailed(error: String?)
}