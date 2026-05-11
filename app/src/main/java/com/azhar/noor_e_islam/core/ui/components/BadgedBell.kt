package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azhar.noor_e_islam.presentation.notifications.NotificationsBadgeViewModel
import com.azhar.noor_e_islam.ui.theme.Emerald900

/**
 * Bell icon with a small count badge.
 * Use inside an [androidx.compose.material3.IconButton]:
 *
 *   IconButton(onClick = onOpenNotifications) { BadgedBell() }
 */
@Composable
fun BadgedBell(
    tint: Color = Emerald900,
    icon: ImageVector = Icons.Outlined.Notifications,
    vm: NotificationsBadgeViewModel = hiltViewModel(),
) {
    val count by vm.unreadCount.collectAsStateWithLifecycle()
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text(if (count > 99) "99+" else count.toString())
                }
            }
        }
    ) {
        Icon(icon, contentDescription = "Notifications", tint = tint)
    }
}

/** Outlined-style variant for screens (Profile) that used [Icons.Outlined.NotificationsNone]. */
@Composable
fun BadgedBellOutlined(tint: Color = Emerald900) =
    BadgedBell(tint = tint, icon = Icons.Outlined.NotificationsNone)

