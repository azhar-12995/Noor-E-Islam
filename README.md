# Noor-e-Islam 🌙

A premium, production-grade Islamic super app built with **Kotlin + Jetpack Compose**, following **Clean Architecture + MVVM + Repository Pattern + Hilt + Firebase**.

> _Learn. Reflect. Live Islam._

---

## ✨ Features

| Module | Highlights |
|---|---|
| **Splash & Onboarding** | Animated crescent, geometric pattern, 3-page pager with skip/next, DataStore persistence |
| **Authentication** | Email/password, register, forgot, anonymous (guest), Google sign-in scaffold, email verification |
| **Home Dashboard** | Greeting + Hijri/Gregorian dates, today's hadith, quick-access grid, continue-reading card |
| **Drawer Navigation** | Profile header, upgrade CTA, daily-goal progress, motivational ayah |
| **Quran Module** | Surah list with Makki/Madani filter + search, ayah reader, settings (font/translation/reciter), share-ayah destination, deep-link `noor://quran/{surahId}` |
| **Calendar** | Hijri (via ICU IslamicCalendar), Gregorian, important Islamic dates list |
| **Incidents / History** | Timeline-style cards (5 seed events) |
| **Dua Collection** | Categorized list (Morning, Evening, Travel, Sleep, …) |
| **Stories** | Ghazwat, Sahaba, Prophets, Heroes — filter chips |
| **Good Habits** | Track, log, streaks, FAB to seed starter habits, Room persistence |
| **Bookmarks / Notes / Progress** | Full Room CRUD; rich note editor |
| **Profile / Settings** | User card, theme/language switch (system/light/dark + en/ur/ar), Firebase backup placeholders |
| **Notifications** | FCM service + default channel registered |

## 🏛 Architecture

```
com.azhar.noor_e_islam
├── core/
│   ├── datastore/        UserPreferences (Proto-style typed prefs)
│   ├── navigation/       Route sealed class
│   ├── ui/components/    GoldButton, IslamicCard, AyahCard, Geometric pattern, Shimmer, States
│   └── util/             Resource, UiState, UiEvent, safeFlow/safeCall, NetworkMonitor, DateUtils
├── data/
│   ├── local/            Room AppDatabase + 9 entities + DAOs
│   ├── remote/
│   │   ├── api/          Retrofit (alquran.cloud, aladhan.com)
│   │   └── firebase/     FcmService
│   ├── mapper/           Entity ↔ Domain
│   └── repository/       Auth, Quran, Bookmark, Note, Habit, Dua, Story, Calendar, Incident, Progress, UserPrefs
├── domain/
│   ├── model/            User, Surah, Ayah, Bookmark, Note, Habit, Dua, Story, Incident, …
│   ├── repository/       Interfaces (offline-first contracts)
│   └── usecase/          Constructor-injected, single-responsibility
├── di/                   Hilt modules: Database, Network, Firebase, Repository, Dispatcher
├── presentation/
│   ├── splash/, onboarding/
│   ├── auth/{login,register,forgot}
│   ├── home/, drawer/, scaffold/, navigation/
│   ├── quran/{list,reader,settings}
│   ├── calendar/, incidents/, dua/, stories/, habits/, learn/
│   ├── bookmarks/, notes/, progress/, profile/, settings/, hadith/
│   └── misc/             SimpleScreen placeholder
├── ui/theme/             Color, Type, Shape, Gradients, Theme (Emerald + Gold)
├── NooreIslamApp.kt      @HiltAndroidApp
└── MainActivity.kt       @AndroidEntryPoint, splashscreen, edge-to-edge
```

## 🎨 Design System

- **Palette**: Emerald `#062B1F → #4FB28A`, Gold `#A78321 → #FAE9B0`, Ivory neutrals.
- **Gradients**: `NoorGradients.Emerald`, `Gold`, `Night`, `Ivory`, plus radial.
- **Shapes**: 4-tier rounded plus `IslamicArchShape`.
- **Typography**: Material 3 scale + reserved `ArabicTextStyle` (drop in Amiri / Noto Naskh Arabic in `res/font/` to upgrade).
- **Components**: `IslamicCard`, `GoldButton`, `AyahCard`, `GeometricPatternBg` (canvas-drawn 8-point stars), `ShimmerBox`, `LoadingState`, `EmptyState`, `ErrorState`.
- **Edge-to-edge** + transparent system bars + automatic light/dark icon polarity.
- **RTL**: `android:supportsRtl="true"` and locales `values-ar/`, `values-ur/`.

## 🔌 Dependencies

Compose BOM 2024.12, Material 3, Navigation Compose, Hilt 2.53 (KSP), Room 2.6, DataStore 1.1, Paging 3.3, Firebase BoM 33.7 (Auth/Firestore/Storage/Analytics/Messaging/Config), Retrofit 2.11 + Moshi + OkHttp, Media3 ExoPlayer, Coil, Lottie Compose, Accompanist, Timber, Kotlinx Serialization, Coroutines.

## 🚀 Setup

1. **Replace `app/google-services.json`** with the real file from Firebase Console (matching package `com.azhar.noor_e_islam`).
2. Sync gradle. Targets: `compileSdk 36.1`, `minSdk 24`, JVM 17.
3. Run on device/emulator.

## 🧠 Notes & Future Work

- The Quran corpus is fetched from **alquran.cloud**; results are cached in Room (offline-first).
- `CalendarRepository.prayerTimes` is a stub — wire `https://api.aladhan.com/v1/timings` with lat/lng.
- Google Sign-In credential helper uses CredentialManager — wire UI flow when distributing.
- Lottie animations: drop JSON into `res/raw/` then `LottieAnimation(...)`.
- Premium subscription gating: integrate Play Billing or Firebase Remote Config feature flags.
- Consider migrating to a multi-module Gradle project once feature count grows; the package boundaries already mirror modules.

## 📜 License

Apache 2.0 (or your preferred license).

