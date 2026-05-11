package com.azhar.noor_e_islam.presentation.hadith

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.data.repository.DailyHadith
import com.azhar.noor_e_islam.ui.theme.ArabicTextStyle
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    onBack: () -> Unit = {},
    viewModel: HadithViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Hadith of the Day",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        state.hadith?.let {
                            Text(
                                "${it.bookName ?: "Hadith"} • #${it.hadithNumber.orEmpty()}",
                                color = Gold500,
                                fontSize = 11.sp,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load() }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Emerald900,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(padding)
            state.error != null -> ErrorState(state.error!!, padding) { viewModel.load() }
            state.hadith != null -> HadithContent(state.hadith!!, padding, fontScale)
        }
    }
}

/* --------------------------- Content --------------------------- */

@Composable
private fun HadithContent(h: DailyHadith, padding: PaddingValues, fontScale: Float) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        ChapterBanner(h)
        Spacer(Modifier.height(16.dp))

        // Arabic
        LanguageCard(label = "العربية", tag = "AR") {
            Text(
                text = h.arabic.ifBlank { "—" },
                style = ArabicTextStyle.copy(fontSize = 22.sp * fontScale, lineHeight = 42.sp * fontScale),
                textAlign = TextAlign.Right,
                color = Emerald900,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(14.dp))

        // English
        LanguageCard(label = "English", tag = "EN") {
            h.englishNarrator?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = Gold500, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
            }
            Text(
                h.english.ifBlank { "—" },
                color = Emerald900,
                fontSize = 16.sp * fontScale,
                lineHeight = 26.sp * fontScale,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.height(14.dp))

        // Urdu
        LanguageCard(label = "اردو", tag = "UR") {
            h.urduNarrator?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    color = Gold500,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(
                h.urdu.ifBlank { "—" },
                color = Emerald900,
                fontSize = 16.sp * fontScale,
                lineHeight = 30.sp * fontScale,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReferencePill("${h.bookName ?: "Hadith"} • #${h.hadithNumber.orEmpty()}")
            h.status?.takeIf { it.isNotBlank() }?.let { StatusPill(it) }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            BottomAction(Icons.Outlined.Share, "Share") {
                val text = buildString {
                    appendLine(h.arabic); appendLine()
                    appendLine(h.english); appendLine()
                    appendLine(h.urdu); appendLine()
                    append("— ${h.bookName ?: "Hadith"} #${h.hadithNumber.orEmpty()}")
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(intent, "Share Hadith"))
            }
            BottomAction(Icons.Outlined.BookmarkBorder, "Save") { /* TODO */ }
        }

        Spacer(Modifier.height(16.dp))
    }
}

/* --------------------------- Pieces --------------------------- */

@Composable
private fun ChapterBanner(h: DailyHadith) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(Emerald900, Emerald900.copy(alpha = 0.85f))))
            .padding(18.dp),
    ) {
        Column {
            Text("Chapter", color = Gold500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                h.chapterEnglish ?: h.headingEnglish ?: "—",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            h.headingArabic?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    style = ArabicTextStyle.copy(fontSize = 16.sp, lineHeight = 28.sp),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(label: String, tag: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Gold500.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(Emerald900),
                    contentAlignment = Alignment.Center,
                ) { Text(tag, color = Gold500, fontWeight = FontWeight.Bold, fontSize = 10.sp) }
                Spacer(Modifier.width(10.dp))
                Text(
                    label,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            OrnamentalDivider()
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ReferencePill(text: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Gold500.copy(alpha = 0.18f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Gold500,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatusPill(status: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Emerald900.copy(alpha = 0.10f)) {
        Text(
            status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Emerald900,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun OrnamentalDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Gold500.copy(alpha = 0.35f)))
        repeat(3) {
            Spacer(Modifier.width(6.dp))
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Gold500))
        }
        Spacer(Modifier.width(6.dp))
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Gold500.copy(alpha = 0.35f)))
    }
}

@Composable
private fun BottomAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Icon(icon, null, tint = Emerald900)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Emerald900)
    }
}

/* --------------------------- States --------------------------- */

@Composable
private fun LoadingState(padding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Emerald900)
            Spacer(Modifier.height(12.dp))
            Text("Loading today's hadith…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorState(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Couldn't load hadith",
                color = Emerald900,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald900, contentColor = Color.White),
            ) { Text("Retry") }
        }
    }
}
