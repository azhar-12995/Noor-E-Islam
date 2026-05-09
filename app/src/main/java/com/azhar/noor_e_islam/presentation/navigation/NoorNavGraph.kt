package com.azhar.noor_e_islam.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.azhar.noor_e_islam.core.navigation.Route
import com.azhar.noor_e_islam.presentation.auth.forgot.ForgotPasswordScreen
import com.azhar.noor_e_islam.presentation.auth.login.LoginScreen
import com.azhar.noor_e_islam.presentation.auth.register.RegisterScreen
import com.azhar.noor_e_islam.presentation.bookmarks.BookmarksScreen
import com.azhar.noor_e_islam.presentation.calendar.CalendarScreen
import com.azhar.noor_e_islam.presentation.dua.DuaScreen
import com.azhar.noor_e_islam.presentation.habits.HabitsScreen
import com.azhar.noor_e_islam.presentation.hadith.HadithScreen
import com.azhar.noor_e_islam.presentation.home.HomeScreen
import com.azhar.noor_e_islam.presentation.incidents.IncidentsScreen
import com.azhar.noor_e_islam.presentation.learn.LearnScreen
import com.azhar.noor_e_islam.presentation.notes.NoteEditorScreen
import com.azhar.noor_e_islam.presentation.notes.NotesScreen
import com.azhar.noor_e_islam.presentation.onboarding.OnboardingScreen
import com.azhar.noor_e_islam.presentation.profile.ProfileScreen
import com.azhar.noor_e_islam.presentation.progress.ProgressScreen
import com.azhar.noor_e_islam.presentation.qibla.QiblaScreen
import com.azhar.noor_e_islam.presentation.prayertimes.PrayerTimesScreen
import com.azhar.noor_e_islam.presentation.quran.list.QuranListScreen
import com.azhar.noor_e_islam.presentation.quran.reader.QuranReaderScreen
import com.azhar.noor_e_islam.presentation.quran.settings.QuranSettingsScreen
import com.azhar.noor_e_islam.presentation.quran.share.ShareAyahScreen
import com.azhar.noor_e_islam.presentation.settings.SettingsScreen
import com.azhar.noor_e_islam.presentation.splash.SplashScreen
import com.azhar.noor_e_islam.presentation.stories.StoriesScreen
import com.azhar.noor_e_islam.presentation.misc.SimpleScreen

@Composable
fun NoorNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues,
    onOpenMenu: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route,
    ) {
        // Bootstrap
        composable(Route.Splash.route) {
            SplashScreen(
                onNavigate = { dest ->
                    navController.navigate(dest) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth
        composable(Route.Login.route) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Route.Register.route) },
                onForgot   = { navController.navigate(Route.Forgot.route) }
            )
        }
        composable(Route.Register.route) {
            RegisterScreen(
                onRegistered = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Forgot.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        // Bottom-nav roots
        composable(Route.Home.route) {
            HomeScreen(
                onOpenSurah = { navController.navigate(Route.QuranReader.create(it)) },
                onOpenQuran = { navController.navigate(Route.QuranList.route) },
                onOpenCalendar = { navController.navigate(Route.Calendar.route) },
                onOpenDua = { navController.navigate(Route.Dua.route) },
                onOpenStories = { navController.navigate(Route.Stories.route) },
                onOpenLearn = { navController.navigate(Route.Learn.route) },
                onOpenMenu = onOpenMenu,
                onOpenHadith = { navController.navigate(Route.Hadith.route) },
                onOpenQibla = { navController.navigate(Route.Qibla.route) },
                onOpenPrayerTimes = { navController.navigate(Route.PrayerTimes.route) },
            )
        }
        composable(Route.QuranList.route) {
            QuranListScreen(
                onOpen = { navController.navigate(Route.QuranReader.create(it)) },
                onSettings = { navController.navigate(Route.QuranSettings.route) }
            )
        }
        composable(Route.Learn.route)    { LearnScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Calendar.route) { CalendarScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Profile.route)  {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNotes = { navController.navigate(Route.Notes.route) },
                onBookmarks = { navController.navigate(Route.Bookmarks.route) },
                onSettings = { navController.navigate(Route.Settings.route) },
                onProgress = { navController.navigate(Route.Progress.route) },
            )
        }

        // Quran sub
        composable(
            route = Route.QuranReader.route,
            arguments = listOf(navArgument("surahId") { type = NavType.IntType }),
            deepLinks = listOf(navDeepLink { uriPattern = "noor://quran/{surahId}" })
        ) { entry ->
            val id = entry.arguments?.getInt("surahId") ?: 1
            QuranReaderScreen(
                surahId = id,
                onBack = { navController.popBackStack() },
                onSettings = { navController.navigate(Route.QuranSettings.route) },
                onShareAyah = { ayah -> navController.navigate(Route.QuranShare.create(id, ayah)) }
            )
        }
        composable(Route.QuranSettings.route) {
            QuranSettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Route.QuranShare.route,
            arguments = listOf(
                navArgument("surahId") { type = NavType.IntType },
                navArgument("ayah")    { type = NavType.IntType }
            )
        ) { entry ->
            val sId  = entry.arguments?.getInt("surahId") ?: 1
            val aNum = entry.arguments?.getInt("ayah") ?: 1
            ShareAyahScreen(surahId = sId, ayahNumber = aNum, onBack = { navController.popBackStack() })
        }

        // Other features
        composable(Route.Hadith.route)     { HadithScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Incidents.route)  { IncidentsScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Dua.route)        { DuaScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Stories.route)    { StoriesScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Habits.route)     { HabitsScreen(onMenu = onOpenMenu) }
        composable(Route.Bookmarks.route)  { BookmarksScreen() }
        composable(Route.Notes.route)      {
            NotesScreen(
                onAdd = { navController.navigate(Route.NoteEditor.create()) },
                onEdit = { id -> navController.navigate(Route.NoteEditor.create(id)) }
            )
        }
        composable(
            route = Route.NoteEditor.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType; defaultValue = "" })
        ) { entry ->
            NoteEditorScreen(
                noteId = entry.arguments?.getString("id").orEmpty().ifEmpty { null },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Progress.route)  { ProgressScreen(onBack = { navController.popBackStack() }) }
        composable(Route.Settings.route)  { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Route.About.route)     { SimpleScreen(title = "About Noor-e-Islam", onBack = { navController.popBackStack() }) }
        composable(Route.Downloads.route) { SimpleScreen(title = "Downloads", onBack = { navController.popBackStack() }) }
        composable(Route.Qibla.route)     { QiblaScreen(onBack = { navController.popBackStack() }) }
        composable(Route.PrayerTimes.route) { PrayerTimesScreen(onBack = { navController.popBackStack() }) }
    }
}

