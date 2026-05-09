package com.azhar.noor_e_islam.di

import android.content.Context
import androidx.room.Room
import com.azhar.noor_e_islam.core.util.Constants
import com.azhar.noor_e_islam.data.local.*
import com.azhar.noor_e_islam.data.remote.api.HijriApi
import com.azhar.noor_e_islam.data.remote.api.HadithApi
import com.azhar.noor_e_islam.data.remote.api.QuranApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun surahDao(db: AppDatabase): SurahDao = db.surahDao()
    @Provides fun ayahDao(db: AppDatabase): AyahDao = db.ayahDao()
    @Provides fun bookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun noteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides fun habitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides fun duaDao(db: AppDatabase): DuaDao = db.duaDao()
    @Provides fun storyDao(db: AppDatabase): StoryDao = db.storyDao()
    @Provides fun progressDao(db: AppDatabase): ProgressDao = db.progressDao()
    @Provides fun incidentDao(db: AppDatabase): IncidentDao = db.incidentDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun moshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides @Singleton
    fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    @Provides @Singleton @Named("quran")
    fun quranRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.QURAN_API_BASE)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton @Named("hijri")
    fun hijriRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.ALADHAN_API_BASE)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun quranApi(@Named("quran") r: Retrofit): QuranApi = r.create(QuranApi::class.java)

    @Provides @Singleton
    fun hijriApi(@Named("hijri") r: Retrofit): HijriApi = r.create(HijriApi::class.java)

    @Provides @Singleton @Named("hadith")
    fun hadithRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.HADITH_API_BASE)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun hadithApi(@Named("hadith") r: Retrofit): HadithApi = r.create(HadithApi::class.java)
}

