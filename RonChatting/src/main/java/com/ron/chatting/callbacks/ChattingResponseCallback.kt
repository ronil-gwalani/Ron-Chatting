package com.ron.chatting.callbacks

interface ChattingResponseCallback {
    fun onProcessStarted()
    fun onErrorFound(error: String?)
    fun onProcessCompleted()

}