package com.azhar.noor_e_islam.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.ui.theme.Gold500
import com.azhar.noor_e_islam.ui.theme.NoorGradients
import kotlinx.coroutines.launch

private data class Page(val titleRes: Int, val descRes: Int, val emoji: String)

private val pages = listOf(
    Page(R.string.onboarding_learn_title,   R.string.onboarding_learn_desc,   "📖"),
    Page(R.string.onboarding_reflect_title, R.string.onboarding_reflect_desc, "🌙"),
    Page(R.string.onboarding_live_title,    R.string.onboarding_live_desc,    "🤲"),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(NoorGradients.EmeraldDeep)) {
        GeometricPatternBg(modifier = Modifier.fillMaxSize(), color = Gold500.copy(alpha = 0.08f))

        Column(modifier = Modifier.fillMaxSize().padding(WindowInsetsPadding())) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    viewModel.complete()
                    onFinished()
                }) { Text(stringResource(R.string.skip), color = Gold500) }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) { page ->
                val p = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(p.emoji, fontSize = androidx.compose.ui.unit.TextUnit.Unspecified, style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = stringResource(p.titleRes),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(p.descRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Indicators
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { i ->
                    val color = if (pagerState.currentPage == i) Gold500 else Color.White.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == i) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                GoldButton(
                    text = stringResource(if (pagerState.currentPage == pages.lastIndex) R.string.get_started else R.string.next),
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            viewModel.complete()
                            onFinished()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WindowInsetsPadding(): PaddingValues = PaddingValues(top = 24.dp)

