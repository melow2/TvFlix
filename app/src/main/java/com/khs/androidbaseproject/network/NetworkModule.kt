package com.khs.androidbaseproject.network

import android.content.Context
import android.os.Looper
import com.android.tvmaze.di.DaggerSet
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.khs.androidbaseproject.BuildConfig
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * @Retension
 * 1) Source: 컴파일 타임에만 유용하며 빌드된 binary에는 포함되지 않습니다. (소스 코드 까지만 유지함, 컴파일하면 사라짐)
 * 개발중에 warning이 뜨는 걸 보이지 않도록 하는 @suppress 와 같이 개발 중에만 유용하고, binary에 포함될 필요는 없는 경우.(컴파일 이후로는 삭제되는 형태)
 * 2) Binary: 컴파일 타임과 binary(.class)에도 포함되지만, reflection(내성 검사를 허용하는 라이브러리 기능 및 세트)을 통해 어노테이션에 접근할 수는 없다.
 * 쉽게 말해서 컴파일하고 .class파일도 유지하지만, 런타임에 클래스를 메모리로 읽어오면 해당 정보는 사라진다.
 * 3) Runtime: 디폴트 값(클래스를 메모리에 읽어왔을 떄 까지 유지한다. 코드에서 이 정보를 바탕으로 특정 로직을 수행할 수 있게 한다.)
 * 4) lazy: val에만 사용가능,getter/setter가 없는 프로퍼티만 사용가능, Non-null,Nullable 둘다 사용가능.
 * 5) Ui 컴포넌트를 바인딩하는 과정에선s lazy가 적절함 .
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-09-21 오후 1:51
 **/

@Retention(AnnotationRetention.BINARY)
@Qualifier
private annotation class InternalApi

@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {
    const val TVMAZE_BASE_URL = "tvmaze_base_url"
    private const val BASE_URL = "https://api.tvmaze.com/"

    @Provides
    @Named(TVMAZE_BASE_URL)
    fun provideBaseUrlString(): String {
        return BASE_URL
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideChuckInterceptor(context: Context): ChuckerInterceptor {
        return ChuckerInterceptor(context)
    }

    // Use newBuilder() to customize so that thread-pool and connection-pool same are used
    @Provides
    fun provideOkHttpClientBuilder(
        @InternalApi okHttpClient: Lazy<OkHttpClient>
    ): OkHttpClient.Builder {
        return okHttpClient.get().newBuilder()
    }

    @InternalApi
    @Provides
    @Singleton
    fun provideBaseOkHttpClient(
        interceptors: DaggerSet<Interceptor>,
        cache: Cache
    ): OkHttpClient {
        check(Looper.myLooper() != Looper.getMainLooper()) { "HTTP client initialized on main thread." }
        val builder = OkHttpClient.Builder()
        builder.interceptors().addAll(interceptors)
        builder.cache(cache)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideCache(context: Context): Cache {
        check(Looper.myLooper() != Looper.getMainLooper()) { "Cache initialized on main thread." }
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cacheDir = context.cacheDir
        return Cache(cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @InternalApi
        okHttpClient: Lazy<OkHttpClient>,
        @Named(TVMAZE_BASE_URL) baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .callFactory { okHttpClient.get().newCall(it)}
            .build()
    }
}