package com.azhar.noor_e_islam.presentation.learn.wazu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
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

private data class WazuStep(val emoji: String, val title: String, val body: String)

private val wazuSteps = listOf(
    WazuStep("🤲", "1. Niyyah & Bismillah",
        "Make the intention in your heart to perform Wudu, and say \u201CBismillah\u201D before starting."),
    WazuStep("🖐️", "2. Wash both hands",
        "Wash both hands up to the wrists three times, making sure water reaches between the fingers."),
    WazuStep("👄", "3. Rinse the mouth",
        "Take a handful of water and rinse the mouth thoroughly three times (Madmadah)."),
    WazuStep("👃", "4. Sniff water into the nose",
        "Sniff water into the nostrils with the right hand and blow it out with the left, three times (Istinshaq)."),
    WazuStep("😊", "5. Wash the face",
        "Wash the entire face three times — from the hairline to the chin, and from one earlobe to the other."),
    WazuStep("💪", "6. Wash both arms",
        "Wash the right arm from the fingertips up to and including the elbow, three times. Then do the same with the left arm."),
    WazuStep("🧠", "7. Wipe the head (Masah)",
        "Wet your hands and pass them over your head once — from the front to the back, then back to the front."),
    WazuStep("👂", "8. Wipe the ears",
        "With the same wetness, insert index fingers into the ears and pass thumbs over the back of the ears."),
    WazuStep("🦶", "9. Wash both feet",
        "Wash the right foot up to and including the ankle, ensuring water reaches between the toes, three times. Then do the same with the left foot."),
    WazuStep("🤲", "10. Du'a after Wudu",
        "Recite: \u201CAsh-hadu allaa ilaaha illallaah, wahdahoo laa shareeka lah, wa ash-hadu anna Muhammadan 'abduhoo wa Rasooluh.\u201D"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WazuScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("How to Perform Wudu", color = Emerald900, fontWeight = FontWeight.Bold) },
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
                            Icon(Icons.Filled.WaterDrop, null, tint = Gold500, modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Wudu · Ablution",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "Step-by-step guide for ritual purification before Salah.",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            itemsIndexed(wazuSteps) { _, step -> WazuCard(step) }
        }
    }
}

@Composable
private fun WazuCard(step: WazuStep) {
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

