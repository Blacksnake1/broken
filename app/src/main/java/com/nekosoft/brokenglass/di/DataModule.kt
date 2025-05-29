package com.nekosoft.brokenglass.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.data.db.Database
import com.nekosoft.brokenglass.data.db.HistoryDao
import com.nekosoft.brokenglass.data.db.WallpaperDao
import com.nekosoft.brokenglass.network.ApiService
import com.nekosoft.brokenglass.repository.HistoryRepository
import com.nekosoft.brokenglass.repository.HistoryRepositoryImp
import com.nekosoft.brokenglass.repository.WallpaperRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val REQUEST_TIME_OUT: Long = 60
    private const val BASE_URL: String =
        "https://raw.githubusercontent.com/ConfigNeko/BrokenScreenApi/main/"

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context) = AppPreference(context)

    // phần dành cho database
    @Provides
    @Singleton
    fun provideMigration(): Migration {
        val migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `history` (`id` INTEGER, `thumb` TEXT, `name` TEXT, `dateTime` TEXT , `translatedText` TEXT ," +
                            "PRIMARY KEY(`id`))"
                )
            }
        }
        return migration
    }


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context, migration: Migration) =
        Room.databaseBuilder(
            context,
            Database::class.java,
            "NekoSoftDatabase"
        ).addMigrations(migration)
            .build()

    @Singleton
    @Provides
    fun provideScreenDao(db: Database) = db.historyDao()

    @Singleton
    @Provides
    fun provideHomeRepository(
        historyDao: HistoryDao,
    ): HistoryRepository = HistoryRepositoryImp(historyDao)

    @Singleton
    @Provides
    fun provideWallpaperDao(db: Database) = db.wallpaperDao()

    @Singleton
    @Provides
    fun provideWallpaperRepositoryImp(
        apiService: ApiService,
        wallpaperDao: WallpaperDao,
    ): WallpaperRepositoryImp = WallpaperRepositoryImp(apiService, wallpaperDao)


    @Provides
    @Singleton
    fun provideHeadersInterceptor() =
        Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .build()
            )
        }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return provideCommonRetrofit(
            provideGson(),
            provideOkHttpClient(
                provideHeadersInterceptor(),
                provideHttpLoggingInterceptor()
            )
        ).create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headersInterceptor: Interceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient {
//        return
//        if (BuildConfig.DEBUG) {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)

            .addInterceptor(headersInterceptor)
            .addNetworkInterceptor(logging)
            .build()
//        } else {
//            OkHttpClient.Builder()
//                .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
//                .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
//                .addInterceptor(headersInterceptor)
//                .build()
//        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls() // To allow sending null values
            .create()
    }

    @Provides
    @Singleton
    fun provideCommonRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(BASE_URL).build()


}