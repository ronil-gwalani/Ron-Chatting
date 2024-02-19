package com.ron.chatting.pushNotificationCalls

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


internal object RetrofitClientFactory {
    private const val BASEURL = "https://fcm.googleapis.com"
    private var retrofit: Retrofit? = null

    @JvmStatic
    fun getRetroFitClient(headerAuthKey: String): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideOkHttpClient(false,  headerAuthKey))
                .build()
        }
        return if (retrofit != null) retrofit else Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(false,  headerAuthKey))
            .build()
    }

    private fun provideOkHttpClient(
        isLargerUpload: Boolean,
        headerAuthKey: String
    ): OkHttpClient {
        val okhttpClientBuilder = OkHttpClient.Builder()
        var timeOutSec = 45
        if (isLargerUpload) {
            timeOutSec = 300
        }
        okhttpClientBuilder.connectTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.readTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.writeTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.addInterceptor(Interceptor { chain ->
            val original: okhttp3.Request = chain.request()
            val request: okhttp3.Request = original.newBuilder()
                .addHeader("Authorization", "key=$headerAuthKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        })
        return okhttpClientBuilder.build()
    }


}
