package com.azhar.noor_e_islam.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.core.datastore.UserPreferences
import com.azhar.noor_e_islam.data.remote.firebase.AnnouncementsFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

/**
 * Lightweight bell-icon badge: counts user-actionable notifications whose
 * timestamp is newer than [UserPreferences.notificationsLastSeenAt].
 *
 * We only count "novel" items — admin announcements and feedback responses
 * — because prayer / hadith / event reminders recur daily and would cause
 * the badge to never zero-out.
 */
@HiltViewModel
class NotificationsBadgeViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val announcements: AnnouncementsFirestoreService,
    private val feedback: FeedbackFirestoreService,
) : ViewModel() {

    val unreadCount: StateFlow<Int> = flow {
        while (true) {
            val seenAt = runCatching { prefs.notificationsLastSeenAt.first() }.getOrDefault(0L)
            val anns = runCatching { announcements.latest(50) }.getOrDefault(emptyList())
            val fbs  = runCatching { feedback.myFeedback() }.getOrDefault(emptyList())
            val count = anns.count { it.createdAt > seenAt } +
                fbs.count { (it.respondedAt ?: 0L) > seenAt }
            emit(count)
            delay(2.minutes)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

