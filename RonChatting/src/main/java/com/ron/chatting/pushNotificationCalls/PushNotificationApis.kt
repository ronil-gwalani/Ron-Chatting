package com.ron.chatting.pushNotificationCalls

import com.ron.chatting.models.RonPushNotificationModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface PushNotificationApis {
//    @Headers("Authorization: key=$myAuthKey", "Content-Type:application/json")
    @POST("fcm/send")
    fun sendMessage(@Body notification: RonPushNotificationModel?): Call<Response<ResponseBody?>?>?


}
