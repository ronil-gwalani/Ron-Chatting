package com.ron.chatting.pushNotificationCalls

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


internal class RetrofitClientFactory {
        private var retrofit: Retrofit? = null
        fun getRetroFitClient(headerAuthKey: String): Retrofit? {
            val baseUrl = "https://fcm.googleapis.com/v1/"
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(provideOkHttpClient(false, headerAuthKey))
                    .build()
            }
            return if (retrofit != null) retrofit else Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideOkHttpClient(false, headerAuthKey))
                .build()
        }

        private fun provideOkHttpClient(
            isLargerUpload: Boolean,
            headerAuthKey: String
        ): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            val okhttpClientBuilder = OkHttpClient.Builder()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            var timeOutSec = 45
            if (isLargerUpload) {
                timeOutSec = 300
            }
            okhttpClientBuilder.connectTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
            okhttpClientBuilder.readTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
            okhttpClientBuilder.writeTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
//            okhttpClientBuilder.addInterceptor(loggingInterceptor)
            okhttpClientBuilder.addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .addHeader("Authorization", "Bearer $headerAuthKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            })
            return okhttpClientBuilder.build()
        }


}
