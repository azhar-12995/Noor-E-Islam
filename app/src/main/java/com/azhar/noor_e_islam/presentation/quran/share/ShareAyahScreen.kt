package com.azhar.noor_e_islam.presentation.quran.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhar.noor_e_islam.ui.theme.ArabicTextStyle
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAyahScreen(
    surahId: Int,
    ayahNumber: Int,
    arabic: String = "فَنُفِخَ فِي ٱلصُّورِ فَإِذَا هُم مِّنَ ٱلْأَجْدَاثِ إِلَىٰ رَبِّهِمْ يَنسِلُونَ",
    transliteration: String = "Wa aqimus-salaata wa aatuz-zakaata war-ka'oo ma'ar-raaki'een.",
    translation: String = "And establish prayer and give zakah, and bow with those who bow down [in worship and obedience].",
    reference: String = "(2:43)",
    onBack: () -> Unit,
) {
    val ctx = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Share Ayah", color = Emerald900, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ayah card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        arabic,
                        style = ArabicTextStyle.copy(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                        color = Emerald900
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        transliteration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald900,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        reference,
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Social row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareButton("WhatsApp",  Icons.AutoMirrored.Filled.Chat,     Color(0xFF25D366)) {
                    sharePlain(ctx, arabic, translation, surahId, ayahNumber, "com.whatsapp")
                }
                ShareButton("Instagram", Icons.Filled.Camera,   Color(0xFFE4405F)) {
                    sharePlain(ctx, arabic, translation, surahId, ayahNumber, "com.instagram.android")
                }
                ShareButton("Facebook",  Icons.Filled.Facebook, Color(0xFF1877F2)) {
                    sharePlain(ctx, arabic, translation, surahId, ayahNumber, "com.facebook.katana")
                }
                ShareButton("Copy Link", Icons.Filled.Link,     Emerald900) {
                    val text = buildShareText(arabic, translation, surahId, ayahNumber)
                    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("Ayah", text))
                }
            }

            Spacer(Modifier.weight(1f))

            // Share Image button
            Button(
                onClick = { sharePlain(ctx, arabic, translation, surahId, ayahNumber, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Emerald900
                )
            ) {
                Text(
                    "Share Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ShareButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Emerald900,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun buildShareText(arabic: String, translation: String, s: Int, a: Int) = buildString {
    append(arabic); append("\n\n"); append(translation)
    append("\n\n— Quran $s:$a\nvia Noor-e-Islam")
}

private fun sharePlain(
    ctx: Context,
    arabic: String,
    translation: String,
    s: Int,
    a: Int,
    targetPackage: String?
) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, buildShareText(arabic, translation, s, a))
        if (targetPackage != null) setPackage(targetPackage)
    }
    runCatching {
        ctx.startActivity(
            Intent.createChooser(send, "Share ayah").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

