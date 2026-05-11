package com.azhar.noor_e_islam.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azhar.noor_e_islam.core.ui.components.ConfirmDialog
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.util.ImageBase64
import com.azhar.noor_e_islam.data.remote.firebase.AnnouncementsFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.FeedbackFirestoreService
import com.azhar.noor_e_islam.data.remote.firebase.UserProfileFirestoreService
import com.azhar.noor_e_islam.ui.theme.Emerald100
import com.azhar.noor_e_islam.ui.theme.Emerald50
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var confirmLogout by remember { mutableStateOf(false) }

    if (confirmLogout) {
        ConfirmDialog(
            title = "Log out",
            message = "Are you sure you want to log out of the admin dashboard?",
            confirmLabel = "Log out",
            onConfirm = { viewModel.logout(onLogout) },
            onDismiss = { confirmLogout = false },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", color = Emerald900, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, null, tint = Emerald900)
                    }
                    IconButton(onClick = { confirmLogout = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            TabBar(state.tab, onSelect = viewModel::setTab, counts = Triple(state.users.size, state.feedback.size, state.announcements.size))
            if (state.isLoading && state.users.isEmpty() && state.feedback.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Emerald900)
                }
            } else {
                when (state.tab) {
                    AdminTab.USERS    -> UsersTab(state.users)
                    AdminTab.FEEDBACK -> FeedbackTab(state, viewModel)
                    AdminTab.ANNOUNCE -> AnnounceTab(state, viewModel)
                }
            }
        }
    }
}

/* ---------------- Tab bar ---------------- */

@Composable
private fun TabBar(current: AdminTab, onSelect: (AdminTab) -> Unit, counts: Triple<Int, Int, Int>) {
    val items = listOf(
        Triple(AdminTab.USERS,    "Users",     Icons.Filled.People)    to counts.first,
        Triple(AdminTab.FEEDBACK, "Feedback",  Icons.Filled.Feedback)  to counts.second,
        Triple(AdminTab.ANNOUNCE, "Announce",  Icons.Filled.Campaign)  to counts.third,
    )
    TabRow(
        selectedTabIndex = items.indexOfFirst { it.first.first == current }.coerceAtLeast(0),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = Emerald900,
    ) {
        items.forEach { (triple, count) ->
            val (tab, label, icon) = triple
            Tab(
                selected = current == tab,
                onClick = { onSelect(tab) },
                icon = { Icon(icon, null) },
                text = { Text("$label ($count)", fontWeight = FontWeight.SemiBold) },
            )
        }
    }
}

/* ---------------- Users ---------------- */

@Composable
private fun UsersTab(users: List<UserProfileFirestoreService.UserSummary>) {
    if (users.isEmpty()) {
        EmptyState("No users yet.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(users, key = { it.uid }) { u -> UserCard(u) }
    }
}

@Composable
private fun UserCard(user: UserProfileFirestoreService.UserSummary) {
    val bmp = remember(user.photoBase64) { ImageBase64.decodeToBitmap(user.photoBase64) }
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Emerald100),
                contentAlignment = Alignment.Center,
            ) {
                if (bmp != null) {
                    Image(bmp.asImageBitmap(), null, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Filled.Person, null, tint = Emerald900)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.name.ifBlank { "Unnamed user" },
                    style = MaterialTheme.typography.titleSmall,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                if (user.email.isNotBlank()) {
                    Text(
                        user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    "uid: ${user.uid.take(10)}…",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold500,
                )
            }
        }
    }
}

/* ---------------- Feedback ---------------- */

@Composable
private fun FeedbackTab(state: AdminUiState, vm: AdminViewModel) {
    if (state.feedback.isEmpty()) {
        EmptyState("No feedback submitted yet.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        state.message?.let { item { Banner(it, isError = false) } }
        state.error?.let   { item { Banner(it, isError = true) } }
        items(state.feedback, key = { it.id }) { f ->
            FeedbackAdminCard(f, state, vm)
        }
    }
}

@Composable
private fun FeedbackAdminCard(
    f: FeedbackFirestoreService.FeedbackEntry,
    state: AdminUiState,
    vm: AdminViewModel,
) {
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (f.isResponded) Emerald100 else Gold500.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    if (f.isResponded) "Responded" else "Open",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (f.isResponded) Emerald900 else Gold500,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault()).format(Date(f.createdAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            f.userName.ifBlank { "Anonymous user" },
            style = MaterialTheme.typography.titleSmall,
            color = Emerald900,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "uid: ${f.uid.take(10)}…",
            style = MaterialTheme.typography.labelSmall,
            color = Gold500,
        )
        Spacer(Modifier.height(8.dp))
        Text(f.text, style = MaterialTheme.typography.bodyMedium, color = Emerald900)

        if (f.images.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(f.images) { b64 ->
                    val bmp = remember(b64) { ImageBase64.decodeToBitmap(b64) }
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp)),
                        )
                    }
                }
            }
        }

        if (!f.adminResponse.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Emerald50)
                    .padding(12.dp),
            ) {
                Column {
                    Text(
                        "Your response",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold500,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(f.adminResponse, style = MaterialTheme.typography.bodyMedium, color = Emerald900)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        if (state.replyDraftFor == f.id) {
            OutlinedTextField(
                value = state.replyDraftText,
                onValueChange = vm::setReplyText,
                placeholder = { Text("Write a reply to this user…") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = vm::cancelReply) { Text("Cancel") }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = vm::sendReply,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald900),
                ) {
                    Icon(Icons.Filled.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Send")
                }
            }
        } else {
            OutlinedButton(onClick = { vm.openReply(f.id) }) {
                Icon(Icons.Filled.Reply, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (f.isResponded) "Reply again" else "Reply")
            }
        }
    }
}

/* ---------------- Announcements ---------------- */

@Composable
private fun AnnounceTab(state: AdminUiState, vm: AdminViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Send announcement",
                    style = MaterialTheme.typography.titleSmall,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.annTitle,
                    onValueChange = vm::setAnnTitle,
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.annMessage,
                    onValueChange = vm::setAnnMessage,
                    label = { Text("Message") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.error?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                state.message?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = Emerald900, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(12.dp))
                GoldButton(
                    text = if (state.isLoading) "Sending…" else "Publish to all users",
                    onClick = vm::publishAnnouncement,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            Text(
                "Recently published",
                style = MaterialTheme.typography.titleMedium,
                color = Emerald900,
                fontWeight = FontWeight.Bold,
            )
        }
        if (state.announcements.isEmpty()) {
            item { EmptyState("No announcements yet.") }
        } else {
            items(state.announcements, key = { it.id }) { a -> AnnouncementRow(a) { vm.deleteAnnouncement(a.id) } }
        }
    }
}

@Composable
private fun AnnouncementRow(a: AnnouncementsFirestoreService.Announcement, onDelete: () -> Unit) {
    var confirmDelete by remember { mutableStateOf(false) }
    if (confirmDelete) {
        ConfirmDialog(
            title = "Delete announcement",
            message = "This will remove it from every user's app. Continue?",
            confirmLabel = "Delete",
            onConfirm = onDelete,
            onDismiss = { confirmDelete = false },
        )
    }
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(a.title, style = MaterialTheme.typography.titleSmall, color = Emerald900, fontWeight = FontWeight.Bold)
                if (a.message.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(a.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.getDefault()).format(Date(a.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold500,
                )
            }
            IconButton(onClick = { confirmDelete = true }) {
                Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/* ---------------- Misc ---------------- */

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Banner(text: String, isError: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isError) MaterialTheme.colorScheme.errorContainer else Emerald50)
            .padding(12.dp),
    ) {
        Text(
            text,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else Emerald900,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// Suppress unused import warning for ImageVector — used by TabBar via Icons.* references.
@Suppress("unused") private val _imageVectorRef: ImageVector? = null

