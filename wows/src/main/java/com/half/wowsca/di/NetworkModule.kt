package com.half.wowsca.di

import com.half.wowsca.data.api.ServerInfoApiService
import com.half.wowsca.data.api.WargamingApiService
import dagger.Module
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServerInfoRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.worldofwarships"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @ServerInfoRetrofit
    fun provideServerInfoRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.worldoftanks")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideServerInfoApiService(@ServerInfoRetrofit retrofit: Retrofit): ServerInfoApiService {
        return retrofit.create(ServerInfoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWargamingApiService(retrofit: Retrofit): WargamingApiService {
        return retrofit.create(WargamingApiService::class.java)
    }
}
