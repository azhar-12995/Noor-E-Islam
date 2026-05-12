package com.azhar.noor_e_islam.presentation.learn.kalmas

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.Emerald900
import com.azhar.noor_e_islam.ui.theme.Gold500

internal data class Kalma(
    val number: Int,
    val name: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val virtue: String,
    @DrawableRes val image: Int? = null,
)

internal val kalmas: List<Kalma> = listOf(
    Kalma(
        number = 1,
        name = "Kalma Tayyab",
        title = "The Word of Purity",
        arabic = "لَا إِلٰهَ إِلَّا اللهُ مُحَمَّدٌ رَّسُولُ اللهِ",
        transliteration = "Laa ilaaha illa-llaahu Muhammadu-r Rasoolu-llah",
        translation = "There is no god but Allah; Muhammad is the Messenger of Allah.",
        virtue = "The First Kalma is the foundation of Islam. It affirms the oneness of Allah (Tawheed) and the prophethood of Muhammad ﷺ. A person enters Islam upon believing and reciting it sincerely.",
        image = R.drawable.first_kalma_detail,
    ),
    Kalma(
        number = 2,
        name = "Kalma Shahadat",
        title = "The Word of Testimony",
        arabic = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
        transliteration = "Ash-hadu allaa ilaaha illa-llaahu wahdahoo laa shareeka lahoo wa ash-hadu anna Muhammadan 'abduhoo wa Rasooluh",
        translation = "I bear witness that there is no god but Allah, He is alone and has no partner, and I bear witness that Muhammad is His servant and messenger.",
        virtue = "Recited in every prayer during Tashahhud. Bears explicit witness to Tawheed and to the servitude and messengership of the Prophet ﷺ.",
    ),
    Kalma(
        number = 3,
        name = "Kalma Tamjeed",
        title = "The Word of Glorification",
        arabic = "سُبْحَانَ اللهِ وَالْحَمْدُ لِلهِ وَلَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
        transliteration = "SubhaanAllaahi wal-hamdu lillaahi wa laa ilaaha illa-llaahu wallaahu akbar wa laa hawla wa laa quwwata illaa billaahil-'aliyyil-'azeem",
        translation = "Glory be to Allah, and praise be to Allah, and there is no god but Allah, and Allah is the Greatest. There is no power and no strength except with Allah, the Most High, the Most Great.",
        virtue = "Contains the Baqiyatus-Salihat (\u201Cthe everlasting good deeds\u201D) mentioned in the Qur'an — these words are beloved to Allah and heavy on the scale of deeds.",
    ),
    Kalma(
        number = 4,
        name = "Kalma Tawheed",
        title = "The Word of Oneness",
        arabic = "لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لَا يَمُوتُ أَبَدًا أَبَدًا، ذُو الْجَلَالِ وَالْإِكْرَامِ بِيَدِهِ الْخَيْرُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
        transliteration = "Laa ilaaha illa-llaahu wahdahoo laa shareeka lah, lahul-mulku wa lahul-hamdu yuhyee wa yumeetu wa huwa hayyun laa yamootu abadan abadaa, dhul-jalaali wal-ikraam, bi-yadihil-khayru wa huwa 'alaa kulli shay'in qadeer",
        translation = "There is no god but Allah, He is alone and has no partner. To Him belongs the kingdom and all praise. He gives life and causes death. He is Ever-Living and will never die. Possessor of Majesty and Honour — in His hand is all good, and He is capable of all things.",
        virtue = "A complete declaration of Allah's absolute oneness, eternity, sovereignty and power. Reciting it strengthens conviction in Tawheed.",
    ),
    Kalma(
        number = 5,
        name = "Kalma Astaghfar",
        title = "The Word of Seeking Forgiveness",
        arabic = "أَسْتَغْفِرُ اللهَ رَبِّي مِنْ كُلِّ ذَنْبٍ أَذْنَبْتُهُ عَمَدًا أَوْ خَطَأً سِرًّا أَوْ عَلَانِيَةً وَأَتُوبُ إِلَيْهِ مِنَ الذَّنْبِ الَّذِي أَعْلَمُ وَمِنَ الذَّنْبِ الَّذِي لَا أَعْلَمُ، إِنَّكَ أَنْتَ عَلَّامُ الْغُيُوبِ وَسَتَّارُ الْعُيُوبِ وَغَفَّارُ الذُّنُوبِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ",
        transliteration = "Astaghfirullaaha rabbee min kulli dhambin adhnabtuhoo 'amadan aw khata-an sirran aw 'alaaniyatan wa atoobu ilayhi min adh-dhambil-ladhee a'lamu wa min adh-dhambil-ladhee laa a'lam. Innaka anta 'allaamul-ghuyoobi wa sattaarul-'uyoobi wa ghaffaarudh-dhunoobi wa laa hawla wa laa quwwata illaa billaahil-'aliyyil-'azeem",
        translation = "I seek forgiveness from Allah my Lord for every sin I have committed — knowingly or unknowingly, secretly or openly — and I turn in repentance to Him from the sin I know and the sin I do not know. You are the Knower of the unseen, the Concealer of faults, and the Forgiver of sins. There is no power and no strength except with Allah, the Most High, the Most Great.",
        virtue = "A comprehensive du'a of repentance. Establishes the habit of seeking forgiveness, which the Prophet ﷺ did over 70 times a day.",
    ),
    Kalma(
        number = 6,
        name = "Kalma Radd-e-Kufr",
        title = "The Word of Rejection of Disbelief",
        arabic = "اللّٰهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ أَنْ أُشْرِكَ بِكَ شَيْئًا وَأَنَا أَعْلَمُ بِهِ وَأَسْتَغْفِرُكَ لِمَا لَا أَعْلَمُ بِهِ، تُبْتُ عَنْهُ وَتَبَرَّأْتُ مِنَ الْكُفْرِ وَالشِّرْكِ وَالْمَعَاصِي كُلِّهَا، وَأَسْلَمْتُ وَأَقُولُ لَا إِلٰهَ إِلَّا اللهُ مُحَمَّدٌ رَّسُولُ اللهِ",
        transliteration = "Allaahumma innee a'oodhu bika min an ushrika bika shay-an wa anaa a'lamu bihee wa astaghfiruka limaa laa a'lamu bih, tubtu 'anhu wa tabarra-tu minal-kufri wash-shirki wal-ma'aasee kullihaa wa aslamtu wa aqoolu laa ilaaha illa-llaahu Muhammadur-Rasoolullaah",
        translation = "O Allah, I seek refuge in You from associating any partner with You knowingly, and I seek Your forgiveness for what I do not know. I repent from it and I dissociate myself from disbelief, polytheism and all sins. I submit to You, and I say: There is no god but Allah, Muhammad is the Messenger of Allah.",
        virtue = "A renunciation of disbelief and polytheism, and a renewed submission to Allah. Useful for renewing one's faith and seeking refuge from unknown sins.",
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
                                kalma.title,
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
            // Hero image — per-kalma if assigned, otherwise the group banner.
            Image(
                painter = painterResource(kalma.image ?: R.drawable.six_kalmas),
                contentDescription = kalma.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )

            // Header
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Gold500.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            kalma.number.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Gold500,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        kalma.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Emerald900,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Arabic
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Arabic",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    kalma.arabic,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Emerald900,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Transliteration
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Transliteration",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    kalma.transliteration,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gold500,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Translation
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Translation",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    kalma.translation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Virtue / Note
            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald900,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    kalma.virtue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
