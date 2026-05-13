package com.azhar.noor_e_islam.presentation.learn.kalmas

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

internal data class Kalma(
    val number: Int,
    val name: String,            // English / Roman name
    val urduName: String,        // Urdu name (e.g. "پہلا کلمہ طیب")
    val arabic: String,
    val urdu: String,            // Urdu translation
    val english: String,         // English translation
)

internal val kalmas: List<Kalma> = listOf(
    Kalma(
        number = 1,
        name = "Kalima Tayyibah",
        urduName = "پہلا کلمہ طیب",
        arabic = "لَا إِلٰهَ إِلَّا اللّٰهُ مُحَمَّدٌ رَّسُوْلُ اللّٰهِ",
        urdu = "اللہ کے سوا کوئی عبادت کے لائق نہیں، محمد ﷺ اللہ کے رسول ہیں۔",
        english = "There is none worthy of worship except God, and Muhammad ﷺ is the Messenger of God.",
    ),
    Kalma(
        number = 2,
        name = "Kalima Shahadat",
        urduName = "دوسرا کلمہ شہادت",
        arabic = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيْكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُوْلُهُ",
        urdu = "میں گواہی دیتا ہوں کہ اللہ کے سوا کوئی عبادت کے لائق نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں، اور میں گواہی دیتا ہوں کہ محمد ﷺ اللہ کے بندے اور اس کے رسول ہیں۔",
        english = "I bear witness that there is none worthy of worship except God; He is One and has no partner. And I bear witness that Muhammad ﷺ is His servant and Messenger.",
    ),
    Kalma(
        number = 3,
        name = "Kalima Tamjeed",
        urduName = "تیسرا کلمہ تمجید",
        arabic = "سُبْحَانَ اللّٰهِ، وَالْحَمْدُ لِلّٰهِ، وَلَا إِلٰهَ إِلَّا اللّٰهُ، وَاللّٰهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللّٰهِ الْعَلِيِّ الْعَظِيْمِ",
        urdu = "اللہ پاک ہے، اور تمام تعریفیں اللہ ہی کے لیے ہیں، اور اللہ کے سوا کوئی عبادت کے لائق نہیں، اور اللہ سب سے بڑا ہے۔ گناہوں سے بچنے کی طاقت اور نیکی کرنے کی قوت اللہ ہی کی طرف سے ہے، جو بہت بلند اور عظمت والا ہے۔",
        english = "Glory be to God, and all praise belongs to God. There is none worthy of worship except God, and God is the Greatest. There is no power and no strength except through God, the Most High, the Most Great.",
    ),
    Kalma(
        number = 4,
        name = "Kalima Tauheed",
        urduName = "چوتھا کلمہ توحید",
        arabic = "لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيْكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِيْ وَيُمِيْتُ، وَهُوَ حَيٌّ لَا يَمُوْتُ أَبَدًا أَبَدًا، ذُو الْجَلَالِ وَالْإِكْرَامِ، بِيَدِهِ الْخَيْرُ، وَهُوَ عَلٰى كُلِّ شَيْءٍ قَدِيْرٌ",
        urdu = "اللہ کے سوا کوئی عبادت کے لائق نہیں، وہ اکیلا ہے، اس کا کوئی شریک نہیں۔ اسی کے لیے بادشاہی ہے اور اسی کے لیے تمام تعریف ہے۔ وہی زندہ کرتا ہے اور موت دیتا ہے، اور وہ ہمیشہ زندہ ہے، اسے کبھی موت نہیں آئے گی۔ وہ عظمت اور بزرگی والا ہے۔ اسی کے ہاتھ میں بھلائی ہے، اور وہ ہر چیز پر قادر ہے۔",
        english = "There is none worthy of worship except God. He is One and has no partner. To Him belongs the kingdom, and to Him belongs all praise. He gives life and causes death, and He is Ever-Living and never dies. He is the Lord of Majesty and Honor. In His hand is all good, and He has power over everything.",
    ),
    Kalma(
        number = 5,
        name = "Kalima Astaghfar",
        urduName = "پانچواں کلمہ استغفار",
        arabic = "أَسْتَغْفِرُ اللّٰهَ رَبِّيْ مِنْ كُلِّ ذَنْبٍ أَذْنَبْتُهُ عَمَدًا أَوْ خَطَأً، سِرًّا أَوْ عَلَانِيَةً، وَأَتُوْبُ إِلَيْهِ مِنَ الذَّنْبِ الَّذِيْ أَعْلَمُ، وَمِنَ الذَّنْبِ الَّذِيْ لَا أَعْلَمُ، إِنَّكَ أَنْتَ عَلَّامُ الْغُيُوْبِ، وَسَتَّارُ الْعُيُوْبِ، وَغَفَّارُ الذُّنُوْبِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللّٰهِ الْعَلِيِّ الْعَظِيْمِ",
        urdu = "میں اللہ سے معافی مانگتا ہوں، جو میرا رب ہے، ہر اس گناہ سے جو میں نے جان بوجھ کر یا بھول کر، چھپ کر یا کھلم کھلا کیا۔ اور میں اس گناہ سے بھی توبہ کرتا ہوں جو مجھے معلوم ہے، اور اس گناہ سے بھی جو مجھے معلوم نہیں۔ بے شک تو ہی غیب کی باتوں کو خوب جاننے والا، عیبوں کو چھپانے والا، اور گناہوں کو بہت بخشنے والا ہے۔ گناہوں سے بچنے کی طاقت اور نیکی کرنے کی قوت اللہ ہی کی طرف سے ہے، جو بہت بلند اور عظمت والا ہے۔",
        english = "I seek forgiveness from God, my Lord, for every sin I have committed knowingly or unknowingly, secretly or openly. I repent to Him for the sins I know and for the sins I do not know. Surely, You are the Knower of the unseen, the Concealer of faults, and the Forgiver of sins. There is no power and no strength except through God, the Most High, the Most Great.",
    ),
    Kalma(
        number = 6,
        name = "Kalima Radd-e-Kufr",
        urduName = "چھٹا کلمہ ردِ کفر",
        arabic = "اللّٰهُمَّ إِنِّيْ أَعُوْذُ بِكَ مِنْ أَنْ أُشْرِكَ بِكَ شَيْئًا وَأَنَا أَعْلَمُ بِهِ، وَأَسْتَغْفِرُكَ لِمَا لَا أَعْلَمُ بِهِ، تُبْتُ عَنْهُ، وَتَبَرَّأْتُ مِنَ الْكُفْرِ وَالشِّرْكِ وَالْكِذْبِ وَالْغِيْبَةِ وَالْبِدْعَةِ وَالنَّمِيْمَةِ وَالْفَوَاحِشِ وَالْبُهْتَانِ وَالْمَعَاصِيْ كُلِّهَا، وَأَسْلَمْتُ، وَأَقُوْلُ: لَا إِلٰهَ إِلَّا اللّٰهُ مُحَمَّدٌ رَّسُوْلُ اللّٰهِ",
        urdu = "اے اللہ! میں تیری پناہ مانگتا ہوں اس بات سے کہ میں جان بوجھ کر کسی چیز کو تیرا شریک بناؤں، اور میں تجھ سے اس چیز کی معافی مانگتا ہوں جس کا مجھے علم نہیں۔ میں نے اس سے توبہ کی، اور میں کفر، شرک، جھوٹ، غیبت، بدعت، چغلی، بے حیائی، بہتان، اور تمام نافرمانیوں سے بیزار ہوا۔ میں اسلام قبول کرتا ہوں، اور کہتا ہوں: اللہ کے سوا کوئی عبادت کے لائق نہیں، محمد ﷺ اللہ کے رسول ہیں۔",
        english = "O God! I seek refuge in You from knowingly associating anything with You, and I seek Your forgiveness for what I do unknowingly. I repent from it, and I distance myself from disbelief, polytheism, lying, backbiting, religious innovation, tale-bearing, indecency, false accusation, and all acts of disobedience. I submit, and I declare: There is none worthy of worship except God, and Muhammad ﷺ is the Messenger of God.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalmasScreen(
    onBack: () -> Unit,
    onOpenKalma: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Six Kalmas", color = Emerald900, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        ) {
            item {
                Text(
                    "The six foundational Kalmas of Iman.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
            itemsIndexed(kalmas) { index, kalma ->
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Gold500.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                kalma.number.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Gold500,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                kalma.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Emerald900,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                kalma.urduName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = { onOpenKalma(index) },
                            colors = ButtonDefaults.textButtonColors(contentColor = Emerald900),
                        ) {
                            Text("View Details", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalmaDetailScreen(index: Int, onBack: () -> Unit) {
    val kalma = kalmas.getOrNull(index) ?: kalmas.first()

    var arabicScale by remember { mutableFloatStateOf(1f) }
    var urduScale by remember { mutableFloatStateOf(1f) }
    var englishScale by remember { mutableFloatStateOf(1f) }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(kalma.name, color = Emerald900, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Emerald900)
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.TextFields, contentDescription = "Font size", tint = Emerald900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        val arabicStyle = MaterialTheme.typography.headlineSmall
        val urduStyle = MaterialTheme.typography.bodyLarge
        val englishStyle = MaterialTheme.typography.bodyLarge

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // --- Premium gradient hero header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NoorGradients.EmeraldDeep)
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Gold500.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            kalma.number.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = Gold500,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            kalma.urduName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Gold500,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            kalma.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // --- Arabic card ---
            SectionCard(label = "Arabic") {
                Text(
                    kalma.arabic,
                    style = arabicStyle.copy(
                        fontSize = arabicStyle.fontSize * arabicScale,
                        lineHeight = arabicStyle.lineHeight * arabicScale * 1.3f,
                    ),
                    color = Emerald900,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // --- Urdu translation card ---
            SectionCard(label = "اردو ترجمہ") {
                Text(
                    kalma.urdu,
                    style = urduStyle.copy(
                        fontSize = urduStyle.fontSize * urduScale,
                        lineHeight = urduStyle.lineHeight * urduScale * 1.4f,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // --- English translation card ---
            SectionCard(label = "English Translation") {
                Text(
                    kalma.english,
                    style = englishStyle.copy(
                        fontSize = englishStyle.fontSize * englishScale,
                        lineHeight = englishStyle.lineHeight * englishScale * 1.35f,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showSettings) {
        FontSizeDialog(
            arabicScale = arabicScale,
            urduScale = urduScale,
            englishScale = englishScale,
            onArabicChange = { arabicScale = it },
            onUrduChange = { urduScale = it },
            onEnglishChange = { englishScale = it },
            onReset = {
                arabicScale = 1f; urduScale = 1f; englishScale = 1f
            },
            onDismiss = { showSettings = false },
        )
    }
}

@Composable
private fun FontSizeDialog(
    arabicScale: Float,
    urduScale: Float,
    englishScale: Float,
    onArabicChange: (Float) -> Unit,
    onUrduChange: (Float) -> Unit,
    onEnglishChange: (Float) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Text Size",
                color = Emerald900,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FontSlider("Arabic",  arabicScale,  onArabicChange)
                FontSlider("Urdu",    urduScale,    onUrduChange)
                FontSlider("English", englishScale, onEnglishChange)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Emerald900),
            ) { Text("Done", fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(
                onClick = onReset,
                colors = ButtonDefaults.textButtonColors(contentColor = Gold500),
            ) { Text("Reset") }
        },
    )
}

@Composable
private fun FontSlider(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = Emerald900,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = Gold500,
                fontWeight = FontWeight.Bold,
            )
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0.8f..2.0f,
            steps = 11,
            colors = SliderDefaults.colors(
                thumbColor = Emerald900,
                activeTrackColor = Emerald900,
            ),
        )
    }
}

@Composable
private fun SectionCard(label: String, content: @Composable () -> Unit) {
    IslamicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Gold500),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = Emerald900,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

