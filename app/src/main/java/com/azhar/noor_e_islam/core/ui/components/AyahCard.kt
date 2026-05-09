package com.azhar.noor_e_islam.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.ui.theme.ArabicTextStyle
import com.azhar.noor_e_islam.ui.theme.Gold500

/** Reusable card for displaying a single ayah / hadith line. */
@Composable
fun AyahCard(
    arabic: String,
    translation: String,
    reference: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isBookmarked: Boolean = false,
    onPlay: (() -> Unit)? = null,
    onBookmark: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
) {
    IslamicCard(modifier = modifier.fillMaxWidth()) {
        // Ayah number badge + reference
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    reference,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = arabic,
            style = ArabicTextStyle,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (translation.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onPlay != null) {
                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) Gold500 else MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (onBookmark != null) {
                IconButton(onClick = onBookmark) {
                    Icon(
                        Icons.Outlined.Bookmark,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Gold500 else MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (onCopy != null) {
                IconButton(onClick = onCopy) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (onShare != null) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
