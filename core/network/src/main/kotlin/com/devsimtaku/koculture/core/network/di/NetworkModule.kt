package com.devsimtaku.koculture.core.network.di

import com.devsimtaku.koculture.core.network.BuildConfig
import com.devsimtaku.koculture.core.network.CulturalEventDataSource
import com.devsimtaku.koculture.core.network.retrofit.KoCultureApi
import com.devsimtaku.koculture.core.network.retrofit.RetrofitCulturalEventDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("serviceKey")
    fun provideServiceKey(): String = BuildConfig.SERVICE_KEY

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://openapi.seoul.go.kr:8088/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideKoCultureApi(retrofit: Retrofit): KoCultureApi {
        return retrofit.create(KoCultureApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCulturalEventDataSource(
        koCultureApi: KoCultureApi,
        @Named("serviceKey") serviceKey: String,
    ): CulturalEventDataSource {
        return RetrofitCulturalEventDataSource(
            koCultureApi = koCultureApi,
            serviceKey = serviceKey,
        )
    }
}
