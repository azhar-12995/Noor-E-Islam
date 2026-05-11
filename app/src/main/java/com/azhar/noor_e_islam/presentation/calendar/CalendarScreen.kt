package com.azhar.noor_e_islam.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.domain.model.EventCategory
import com.azhar.noor_e_islam.domain.model.IslamicEvent
import com.azhar.noor_e_islam.ui.theme.Emerald100
import com.azhar.noor_e_islam.ui.theme.Emerald50
import com.azhar.noor_e_islam.ui.theme.Emerald700
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold100
import com.azhar.noor_e_islam.ui.theme.Gold500
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Islamic Calendar", color = Emerald900, fontWeight = FontWeight.Bold) },
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
            // ===== Hero calendar card (visually separated from the rest) =====
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(androidx.compose.ui.graphics.Color.White)
                    .border(
                        width = 1.dp,
                        color = Emerald100,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ----- Month header -----
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = viewModel::prevMonth) {
                            Icon(Icons.Outlined.ChevronLeft, null, tint = Emerald900)
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${monthName(state.visibleMonth)} ${state.visibleYear}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Emerald900,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${state.monthEvents.values.sumOf { it.size }} Islamic events this month",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald700
                            )
                        }
                        IconButton(onClick = viewModel::nextMonth) {
                            Icon(Icons.Outlined.ChevronRight, null, tint = Emerald900)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    MonthGrid(
                        year = state.visibleYear,
                        month = state.visibleMonth,
                        today = state.today,
                        selected = state.selected,
                        monthEvents = state.monthEvents,
                        onSelect = viewModel::selectDate,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            val sel = state.selected
            val isToday = sel == state.today
            Text(
                text = if (isToday) "Today's Events"
                       else "Events on ${sel.third} ${monthName(sel.second)} ${sel.first}",
                style = MaterialTheme.typography.titleMedium,
                color = Emerald900,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            if (state.selectedDayEvents.isEmpty()) {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No Islamic events on this day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald900
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tap any highlighted date or scroll months to discover events.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                state.selectedDayEvents.forEach { ev ->
                    EventCard(ev); Spacer(Modifier.height(10.dp))
                }
            }

            if (state.upcoming.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Upcoming",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                state.upcoming.forEach { ev ->
                    UpcomingRow(ev); Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

/* ---------- Month grid ---------- */

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    today: Triple<Int, Int, Int>,
    selected: Triple<Int, Int, Int>,
    monthEvents: Map<Int, List<IslamicEvent>>,
    onSelect: (Int, Int, Int) -> Unit,
) {
    val cal = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val cells: List<Int?> = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (d in 1..daysInMonth) add(d)
    }

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

    cells.chunked(7).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row.forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    DayCell(
                        day = day,
                        isToday = day != null && Triple(year, month, day) == today,
                        isSelected = day != null && Triple(year, month, day) == selected,
                        events = day?.let { monthEvents[it] }.orEmpty(),
                        onClick = { if (day != null) onSelect(year, month, day) },
                    )
                }
            }
            repeat(7 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun DayCell(
    day: Int?,
    isToday: Boolean,
    isSelected: Boolean,
    events: List<IslamicEvent>,
    onClick: () -> Unit,
) {
    val hasEvents = events.isNotEmpty()
    val bgColor = when {
        isToday -> Emerald900
        isSelected -> Emerald100
        hasEvents -> Gold100
        else -> Color.Transparent
    }
    val fgColor = if (isToday) Color.White else Emerald900

    Column(
        modifier = Modifier
            .size(width = 44.dp, height = 50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(if (day != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (day != null) {
            Text(
                day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = fgColor,
                fontWeight = if (isToday || hasEvents) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                events.take(3).forEach { ev ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isToday) Gold500 else categoryColor(ev.category))
                    )
                }
            }
        }
    }
}

/* ---------- Event cards ---------- */

@Composable
private fun EventCard(event: IslamicEvent) {
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(categoryColor(event.category).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(event.category),
                    contentDescription = null,
                    tint = categoryColor(event.category)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    event.hijriDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold
                )
                if (event.weekday.isNotBlank()) {
                    Text(
                        event.weekday,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (event.description.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Emerald50)
                    .padding(12.dp)
            ) {
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald900,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun UpcomingRow(event: IslamicEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Emerald100, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Emerald900),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                event.day.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                monthName(event.month).take(3),
                style = MaterialTheme.typography.labelSmall,
                color = Gold500,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = Emerald900, fontWeight = FontWeight.SemiBold)
            Text(
                event.hijriDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = categoryIcon(event.category),
            contentDescription = null,
            tint = categoryColor(event.category)
        )
    }
}

/* ---------- Helpers ---------- */

private fun monthName(month: Int): String = listOf(
    "", "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
).getOrElse(month) { "" }

private fun categoryColor(c: EventCategory): Color = when (c) {
    EventCategory.EID         -> Color(0xFFD4AF37)
    EventCategory.HOLY_NIGHT  -> Color(0xFF6E4FA8)
    EventCategory.RAMADAN     -> Color(0xFF1B7A56)
    EventCategory.HAJJ        -> Color(0xFFB07B2A)
    EventCategory.MUHARRAM    -> Color(0xFF8C2A2A)
    EventCategory.MILAD       -> Color(0xFF0F4D38)
    EventCategory.REMEMBRANCE -> Color(0xFF345C99)
    EventCategory.GENERAL     -> Emerald700
}

private fun categoryIcon(c: EventCategory): ImageVector = when (c) {
    EventCategory.EID         -> Icons.Filled.Celebration
    EventCategory.HOLY_NIGHT  -> Icons.Filled.Brightness2
    EventCategory.RAMADAN     -> Icons.Filled.Star
    EventCategory.HAJJ        -> Icons.Filled.Mosque
    EventCategory.MUHARRAM    -> Icons.Filled.LocalFireDepartment
    EventCategory.MILAD       -> Icons.Filled.AutoAwesome
    EventCategory.REMEMBRANCE -> Icons.Filled.EventNote
    EventCategory.GENERAL     -> Icons.Filled.EventNote
}
