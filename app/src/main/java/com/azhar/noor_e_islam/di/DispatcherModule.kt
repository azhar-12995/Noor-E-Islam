package com.azhar.noor_e_islam.di

import com.azhar.noor_e_islam.core.util.DefaultDispatcher
import com.azhar.noor_e_islam.core.util.IoDispatcher
import com.azhar.noor_e_islam.core.util.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides @IoDispatcher      fun ioDispatcher():      CoroutineDispatcher = Dispatchers.IO
    @Provides @MainDispatcher    fun mainDispatcher():    CoroutineDispatcher = Dispatchers.Main
    @Provides @DefaultDispatcher fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

