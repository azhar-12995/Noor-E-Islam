package com.azhar.noor_e_islam.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides @Singleton fun auth(): FirebaseAuth = Firebase.auth
    @Provides @Singleton fun firestore(): FirebaseFirestore = Firebase.firestore
    @Provides @Singleton fun storage(): FirebaseStorage = Firebase.storage
    @Provides @Singleton fun analytics(): FirebaseAnalytics = Firebase.analytics
    @Provides @Singleton fun messaging(): FirebaseMessaging = Firebase.messaging
}

