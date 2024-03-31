package com.ron.chatting.pushNotificationCalls

import com.ron.chatting.models.RonPushNewNotificationModel
import com.ron.chatting.models.RonPushNotificationModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface PushNotificationApis {
//    @Headers("Authorization: key=$myAuthKey", "Content-Type:application/json")
    @POST("fcm/send")
    fun sendMessage(@Body notification: RonPushNotificationModel?): Call<Response<ResponseBody?>?>?

    @POST("projects/{url}/messages:send")
   suspend fun sendMessageWithV1Api(@Path("url",encoded = true) url:String, @Body notification: RonPushNewNotificationModel): Response<ResponseBody?>?

}
