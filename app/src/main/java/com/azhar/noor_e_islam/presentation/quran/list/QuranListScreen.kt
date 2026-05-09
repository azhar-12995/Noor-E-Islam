package com.azhar.noor_e_islam.presentation.quran.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.core.ui.components.ErrorState
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.ui.components.LoadingState
import com.azhar.noor_e_islam.domain.model.RevelationType
import com.azhar.noor_e_islam.domain.model.Surah
import com.azhar.noor_e_islam.ui.theme.ArabicTextStyle
import com.azhar.noor_e_islam.ui.theme.Gold500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranListScreen(
    onOpen: (Int) -> Unit,
    onSettings: () -> Unit,
    viewModel: QuranListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            // Search field lives directly in the app bar — sits flush against the status bar.
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQuery,
                        placeholder = { Text("Search surah…") },
                        leadingIcon = { Icon(Icons.Outlined.Search, null) },
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQuery("") }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                },
                actions = {
                    IconButton(onClick = onSettings) { Icon(Icons.Outlined.Settings, "Settings") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            state.isLoading        -> LoadingState(modifier = Modifier.padding(padding))
            state.error != null    -> ErrorState(message = state.error!!, modifier = Modifier.padding(padding))
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            ) {
                items(viewModel.filtered, key = { it.number }) { surah ->
                    SurahRow(surah = surah, onClick = { onOpen(surah.number) })
                }
            }
        }
    }
}

@Composable
private fun SurahRow(surah: Surah, onClick: () -> Unit) {
    IslamicCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.number.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    surah.englishName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${surah.englishMeaning} • ${surah.ayahCount} ayahs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                RevelationBadge(surah.revelationType)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = surah.name,
                style = ArabicTextStyle.copy(fontSize = 24.sp, lineHeight = 32.sp),
                textAlign = TextAlign.End,
                color = Gold500
            )
        }
    }
}

@Composable
private fun RevelationBadge(type: RevelationType) {
    val isMakki = type == RevelationType.MAKKI
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isMakki) Gold500.copy(alpha = 0.18f)
                else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isMakki) "Makki" else "Madani",
            style = MaterialTheme.typography.labelSmall,
            color = if (isMakki) Gold500 else MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Medium
        )
    }
}
