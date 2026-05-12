package com.azhar.noor_e_islam.presentation.learn.pillars

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

internal data class Pillar(
    val title: String,
    val arabic: String,
    val subtitle: String,
    val emoji: String,
    val summary: String,
    val details: List<String>,
    @DrawableRes val image: Int? = null,
)

internal val pillars: List<Pillar> = listOf(
    Pillar(
        title = "Shahada",
        arabic = "الشَّهَادَة",
        subtitle = "Declaration of Faith",
        emoji = "🕋",
        summary = "The testimony that there is no deity worthy of worship except Allah, and that Muhammad ﷺ is the Messenger of Allah.",
        details = listOf(
            "Arabic: \u200Fأَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللهِ",
            "Translation: I bear witness that there is no god but Allah, and I bear witness that Muhammad is the Messenger of Allah.",
            "It is the foundation of Islam. A person enters Islam by sincerely uttering and believing in the Shahada.",
            "It affirms two truths: pure monotheism (Tawheed) and the prophethood of Muhammad ﷺ as the final messenger.",
        ),
        image = R.drawable.first_pillar,
    ),
    Pillar(
        title = "Salah",
        arabic = "الصَّلَاة",
        subtitle = "Five Daily Prayers",
        emoji = "🕌",
        summary = "Performing the five obligatory prayers each day at their appointed times.",
        details = listOf(
            "The five prayers are: Fajr, Dhuhr, Asr, Maghrib and Isha.",
            "Salah is a direct connection between the worshipper and Allah, without any intermediary.",
            "It is the first deed a Muslim will be asked about on the Day of Judgement.",
            "Wudu (ritual purification) is required before prayer, and one must face the Qibla in Makkah.",
        ),
        image = R.drawable.second_pillar,
    ),
    Pillar(
        title = "Zakat",
        arabic = "الزَّكَاة",
        subtitle = "Obligatory Charity",
        emoji = "🤲",
        summary = "Giving a fixed share (2.5%) of one's qualifying wealth annually to those in need.",
        details = listOf(
            "Zakat literally means \u201Cpurification\u201D — it purifies wealth and the heart from greed.",
            "It is due on wealth that exceeds the nisab (minimum threshold) and has been held for a lunar year.",
            "The Qur'an lists eight categories of eligible recipients (At-Tawbah 9:60).",
            "It strengthens social solidarity and reduces inequality in the Muslim community.",
        ),
        image = R.drawable.third_pillar,
    ),
    Pillar(
        title = "Sawm",
        arabic = "الصَّوْم",
        subtitle = "Fasting in Ramadan",
        emoji = "🌙",
        summary = "Abstaining from food, drink and intimate relations from dawn (Fajr) to sunset (Maghrib) during the month of Ramadan.",
        details = listOf(
            "Ramadan is the 9th month of the Islamic lunar calendar.",
            "Fasting cultivates God-consciousness (taqwa), self-discipline and empathy for the poor.",
            "Children, the sick, travellers, the elderly, and menstruating women are exempted (some make up later).",
            "The night of Laylat al-Qadr in the last ten nights is described as \u201Cbetter than a thousand months\u201D.",
        ),
        image = R.drawable.fourth_pillar,
    ),
    Pillar(
        title = "Hajj",
        arabic = "الْحَجّ",
        subtitle = "Pilgrimage to Makkah",
        emoji = "🕋",
        summary = "Performing the pilgrimage to the Sacred House in Makkah once in a lifetime, for those who are physically and financially able.",
        details = listOf(
            "Hajj is performed in the month of Dhul-Hijjah and includes Tawaf around the Kaaba, Sa'i between Safa and Marwah, and standing at Arafah.",
            "It commemorates the legacy of Prophet Ibrahim عليه السلام, his wife Hajar, and their son Isma'il عليه السلام.",
            "It is a powerful reminder of human equality — all pilgrims wear simple Ihram garments.",
            "A Hajj accepted by Allah erases sins, returning the pilgrim as the day his mother gave birth to him.",
        ),
        image = R.drawable.fifth_pillar,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillarsScreen(
    onBack: () -> Unit,
    onOpenPillar: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pillars of Islam", color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        ) {
            itemsIndexed(pillars) { index, pillar ->
                Image(
                    painter = painterResource(pillar.image ?: R.drawable.pillers_of_islam),
                    contentDescription = pillar.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenPillar(index) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillarDetailScreen(index: Int, onBack: () -> Unit) {
    val pillar = pillars.getOrNull(index) ?: pillars.first()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(pillar.title, color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Hero image — uses the per-pillar drawable if provided, otherwise the group banner.
            Image(
                painter = painterResource(pillar.image ?: R.drawable.pillers_of_islam),
                contentDescription = pillar.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )

            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Gold500.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pillar.emoji, style = MaterialTheme.typography.displaySmall)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        pillar.arabic,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Gold500,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        pillar.subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    pillar.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Key Points",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                pillar.details.forEach { d ->
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Gold500),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            d,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            // Trailing spacer for system nav
            Spacer(Modifier.height(24.dp))
            // Avoid unused-warning for Color import
            Box(Modifier.size(0.dp).background(Color.Transparent))
        }
    }
}

