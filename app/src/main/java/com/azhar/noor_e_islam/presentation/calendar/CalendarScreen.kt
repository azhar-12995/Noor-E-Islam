package com.azhar.noor_e_islam.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.util.DateUtils
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Islamic Calendar", color = Emerald900, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.NotificationsNone, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 88.dp)
        ) {
            // Month header with chevrons
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.ChevronLeft, null, tint = Emerald900)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        DateUtils.hijriToday(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Emerald900,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        DateUtils.gregorianToday(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.ChevronRight, null, tint = Emerald900)
                }
            }
            Spacer(Modifier.height(8.dp))

            MonthGrid()

            Spacer(Modifier.height(20.dp))

            Text(
                "Today's Importance",
                style = MaterialTheme.typography.titleMedium,
                color = Emerald900,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Dhul-Qi'dah is one of the four Haram months in which Allah forbids oppression and injustice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald900
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "(Tafsir Ibn Kathir)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MonthGrid() {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0..6 (Sun..Sat)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // Build cells: leading blanks + days
    val cells: List<Int?> = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (d in 1..daysInMonth) add(d)
    }

    // Day-of-week header
    val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        weekdays.forEach { day ->
            Text(
                day,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    Spacer(Modifier.height(8.dp))

    // Grid of day numbers — Row-based to coexist with verticalScroll parent.
    val rows = cells.chunked(7)
    rows.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row.forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    DayCell(day = day, isToday = day == today)
                }
            }
            // pad last row to 7 cells
            repeat(7 - row.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DayCell(day: Int?, isToday: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isToday) Emerald900 else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Text(
                day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isToday) Color.White else Emerald900,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
