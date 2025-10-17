package com.example.signlink.data.di

import com.example.signlink.data.services.CustomerService
import com.example.signlink.data.repository.AuthRepository
import com.example.signlink.data.repository.KamusRepository
import com.example.signlink.data.services.KamusService
import com.example.signlink.data.utils.AuthUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:7777/api/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("X-Frontend", "main")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
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
    fun provideCustomerService(retrofit: Retrofit): CustomerService {
        return retrofit.create(CustomerService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(service: CustomerService): AuthRepository {
        return AuthRepository(service)
    }

    @Provides
    @Singleton
    fun provideKamusService(retrofit: Retrofit): KamusService {
        return retrofit.create(KamusService::class.java)
    }

    @Provides
    @Singleton
    fun provideKamusRepository(service: KamusService): KamusRepository {
        return KamusRepository(service)
    }
}
