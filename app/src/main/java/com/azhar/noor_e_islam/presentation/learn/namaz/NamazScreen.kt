package com.azhar.noor_e_islam.presentation.learn.namaz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mosque
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
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients

private data class Step(val emoji: String, val title: String, val body: String)

private val namazSteps = listOf(
    Step("🧎",  "1. Niyyah (Intention)",
        "Stand facing the Qibla and silently make the intention in your heart for the prayer you are about to perform (e.g. \u201CFajr — 2 rakat fard\u201D)."),
    Step("🙌",  "2. Takbir-e-Tahrima",
        "Raise both hands up to your ears (men) or shoulders (women) and say \u201CAllahu Akbar\u201D, then fold them on your chest/below the navel."),
    Step("📖",  "3. Qiyam (Standing)",
        "Recite Sana, then Ta'awwuz, Tasmiyah, Surah Al-Fatiha, and a portion of any other Surah from the Qur'an."),
    Step("🙇",  "4. Ruku' (Bowing)",
        "Say \u201CAllahu Akbar\u201D and bend forward, placing your hands on your knees. Recite \u201CSubhana Rabbiyal Azeem\u201D three times."),
    Step("🧍",  "5. Qauma (Standing after Ruku')",
        "Rise and say \u201CSami'Allahu liman hamidah, Rabbana lakal hamd.\u201D Stand fully upright."),
    Step("🕋",  "6. Sajdah (Prostration)",
        "Say \u201CAllahu Akbar\u201D and prostrate placing forehead, nose, both palms, knees and toes on the ground. Recite \u201CSubhana Rabbiyal A'la\u201D three times. Sit briefly, then perform a second sajdah the same way."),
    Step("🪑",  "7. Qa'dah (Sitting — Tashahhud)",
        "After the second rak'ah, sit and recite the Tashahhud (At-Tahiyyat). In the final sitting also recite Durood-e-Ibrahim and a du'a."),
    Step("👋",  "8. Salam",
        "End the prayer by turning the head to the right and then to the left, saying \u201CAssalamu alaikum wa rahmatullah\u201D each time."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("How to Pray (Namaz)", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(NoorGradients.EmeraldDeep)
                        .padding(20.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Gold500.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Mosque, null, tint = Gold500, modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Salah · The Five Daily Prayers",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "Step-by-step guide for performing the prayer.",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            itemsIndexed(namazSteps) { _, step -> StepCard(step) }
        }
    }
}

@Composable
private fun StepCard(step: Step) {
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Gold500.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(step.emoji, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    step.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    step.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

