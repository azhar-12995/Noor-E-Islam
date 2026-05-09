package com.azhar.noor_e_islam.presentation.incidents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = Emerald900,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Incident on This Day", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.NotificationsNone, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Emerald900
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(NoorGradients.Night)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Date pill at top
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Gold500.copy(alpha = 0.18f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "6 AH / 17 Dhul-Qi'dah",
                    style = MaterialTheme.typography.labelLarge,
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(20.dp))

            // Hero image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8B6F47),
                                Color(0xFF5D4A33),
                                Emerald900
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🕌",
                    style = MaterialTheme.typography.displayLarge,
                    color = Gold500
                )
            }
            Spacer(Modifier.height(24.dp))

            Text(
                "Treaty of Hudaybiyyah",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "A historic treaty was signed between the Muslims and Quraysh at Hudaybiyyah. " +
                "It was a turning point for the Muslims.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(28.dp))

            // Read Full Story button — gold pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Gold500)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Read Full Story",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
