package com.azhar.noor_e_islam.data.repository

import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.core.util.safeCall
import com.azhar.noor_e_islam.domain.model.User
import com.azhar.noor_e_islam.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toDomain()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Resource<User> = safeCall {
        auth.signInWithEmailAndPassword(email, password).await()
        auth.currentUser!!.toDomain()
    }

    override suspend fun register(name: String, email: String, password: String): Resource<User> = safeCall {
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name).build()
        ).await()
        auth.currentUser!!.sendEmailVerification().await()
        auth.currentUser!!.toDomain()
    }

    override suspend fun forgotPassword(email: String): Resource<Unit> = safeCall {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signInAnonymously(): Resource<User> = safeCall {
        auth.signInAnonymously().await()
        auth.currentUser!!.toDomain()
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<User> = safeCall {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        auth.currentUser!!.toDomain()
    }

    override suspend fun sendEmailVerification(): Resource<Unit> = safeCall {
        auth.currentUser?.sendEmailVerification()?.await()
        Unit
    }

    override suspend fun logout() { auth.signOut() }

    private fun com.google.firebase.auth.FirebaseUser.toDomain() = User(
        uid = uid,
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
        isAnonymous = isAnonymous,
        emailVerified = isEmailVerified
    )
}

