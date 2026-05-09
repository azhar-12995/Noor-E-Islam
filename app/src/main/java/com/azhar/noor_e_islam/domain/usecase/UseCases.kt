package com.azhar.noor_e_islam.domain.usecase

import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.model.*
import com.azhar.noor_e_islam.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/* ---- Auth ---- */
class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}
class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) = repo.register(name, email, password)
}
class ForgotPasswordUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String) = repo.forgotPassword(email)
}
class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
class ObserveAuthStateUseCase @Inject constructor(private val repo: AuthRepository) {
    operator fun invoke(): Flow<User?> = repo.currentUser
}
class SignInAnonymouslyUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.signInAnonymously()
}

/* ---- Quran ---- */
class GetSurahsUseCase @Inject constructor(private val repo: QuranRepository) {
    operator fun invoke(): Flow<Resource<List<Surah>>> = repo.getSurahs()
}
class GetAyahsUseCase @Inject constructor(private val repo: QuranRepository) {
    operator fun invoke(surah: Int): Flow<Resource<List<Ayah>>> = repo.getAyahs(surah)
}
class UpdateLastReadUseCase @Inject constructor(private val repo: QuranRepository) {
    suspend operator fun invoke(surah: Int, ayah: Int) = repo.updateLastRead(surah, ayah)
}

/* ---- Prefs ---- */
class GetUserPrefsUseCase @Inject constructor(private val repo: UserPrefsRepository) {
    operator fun invoke() = repo.prefs
}
class SetOnboardingDoneUseCase @Inject constructor(private val repo: UserPrefsRepository) {
    suspend operator fun invoke(done: Boolean) = repo.setOnboardingDone(done)
}

/* ---- Bookmarks / Notes / Habits / Duas / Stories ---- */
class GetBookmarksUseCase @Inject constructor(private val repo: BookmarkRepository) {
    operator fun invoke() = repo.observeAll()
}
class GetNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke() = repo.observeAll()
}
class GetHabitsUseCase @Inject constructor(private val repo: HabitRepository) {
    operator fun invoke() = repo.observeAll()
}
class GetDuasUseCase @Inject constructor(private val repo: DuaRepository) {
    operator fun invoke() = repo.all()
}
class GetStoriesUseCase @Inject constructor(private val repo: StoryRepository) {
    operator fun invoke() = repo.all()
}
class GetIncidentsUseCase @Inject constructor(private val repo: IncidentRepository) {
    operator fun invoke() = repo.all()
}
class GetReadingProgressUseCase @Inject constructor(private val repo: ProgressRepository) {
    operator fun invoke() = repo.observe()
}

