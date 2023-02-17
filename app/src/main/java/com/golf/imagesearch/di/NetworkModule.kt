package com.golf.imagesearch.di

import android.util.Log
import com.golf.imagesearch.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.golf.imagesearch.network.interceptor.NormalInterceptor
import com.golf.imagesearch.network.service.ImageService
import com.golf.imagesearch.repository.ImageRepository
import com.golf.imagesearch.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .beautyLogger("OKHTTP")
            .addInterceptor(NormalInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.API_BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideImageService(retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

    @Singleton
    @Provides
    fun providesImageRepository(apiService: ImageService) = ImageRepository(apiService)

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NormalInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NormalInterceptorOkHttpClientConfig

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitNormal

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitConfig

fun OkHttpClient.Builder.beautyLogger(tag: String): OkHttpClient.Builder {
    val logInterceptor = HttpLoggingInterceptor(BeautyLogger(tag))
    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
    addNetworkInterceptor(logInterceptor)
    return this
}

class BeautyLogger(
    private val tag: String,
    private val enable: Boolean = BuildConfig.DEBUG,
    val print: (String) -> Unit = { Log.d(tag, it) },
    val gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (!enable || message.trim().isEmpty()) {
            return
        }
        if ((message.startsWith("{") && message.endsWith("}")) || (message.startsWith("[") && message.endsWith(
                "]"
            ))
        ) {
            print("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄")
            val json = gson.toJson(JSONObject(message))
            val prints = json.toString().split("\n")
            for (p in prints) {
                print("│ $p")
            }
        } else {
            when {
                message.startsWith("<-- HTTP FAILED") -> {
                    print("┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
                    print("│ $message (٭°̧̧̧ω°̧̧̧٭) ")
                    print("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
                }
                message.startsWith("--> END") || message.startsWith("<-- END") -> {
                    print("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄")
                    print("│ $message")
                    print("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
                }
                message.startsWith("-->") || message.startsWith("<--") -> {
                    print("┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
                    print("│ $message ( ͡° ͜ʖ ͡°)")
                    print("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄")
                }
                else -> {
                    print("│ $message")
                }
            }
        }
    }
}