package com.example.healthconnect.codelab.di

import com.example.healthconnect.codelab.data.service.DittoService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.util.Base64
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    //TODO: Move URL, user and pwd to somewhere else

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val auth = "${System.getProperty("DITTO_USER")}:${System.getProperty("DITTO_PWD")}"
            val base64auth = Base64.getEncoder().encodeToString(auth.toByteArray())

            val originalRequest = chain.request()
            val modifiedRequest = originalRequest.newBuilder()
                .header("ngrok-skip-browser-warning", "")
                .header("Authorization", "Basic ${base64auth}")
                .build()
            return chain.proceed(modifiedRequest)
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(System.getProperty("DITTO_URL"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideDittoService(retrofit: Retrofit): DittoService {
        return retrofit.create(DittoService::class.java)
    }
}