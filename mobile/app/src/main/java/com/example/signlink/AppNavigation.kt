package com.example.signlink

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.signlink.screens.HomeScreen
import com.example.signlink.screens.kamus.KamusScreen
import com.example.signlink.screens.kamus.KamusListScreen
import com.example.signlink.screens.kamus.KamusDetailScreen
import com.example.signlink.screens.kuis.KuisScreen
import com.example.signlink.screens.ProfileScreen
import com.example.signlink.screens.VoiceToTextScreen
import com.example.signlink.screens.SplashScreen
import com.example.signlink.screens.OpeningScreen
import com.example.signlink.screens.onboarding.OnboardingScreen
import com.example.signlink.screens.auth.LoginScreen
import com.example.signlink.screens.auth.SignUpScreen
import com.example.signlink.screens.kuis.KuisDetailScreen
import com.example.signlink.viewmodel.AuthViewModel
import com.google.accompanist.navigation.animation.composable

object Destinations {
    const val SPLASH_SCREEN = "splash_screen"
    const val ONBOARDING = "onboarding_screen"
    const val OPENING_SCREEN = "opening_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val SIGNUP_SCREEN = "signup_screen"
    const val HOME_SCREEN = "home_screen"
    const val VTT_SCREEN = "vtt_screen"
    const val KAMUS_SCREEN = "kamus_screen"
    const val KUIS_SCREEN = "kuis_screen"
    const val KUIS_DETAIL_SCREEN = "kuis_detail_screen"
    const val KAMUS_DETAIL_SCREEN = "kamus_detail_screen"
    const val PROFILE_SCREEN = "profile_screen"
}

@OptIn(ExperimentalAnimationApi::class)
@Suppress("DEPRECATION")
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf(Destinations.SPLASH_SCREEN) }
    val viewModel: AuthViewModel = hiltViewModel()

    // Cek JWT saat pertama kali
    LaunchedEffect(Unit) {
        viewModel.checkJwt(context) { isValid ->
            startDestination = if (isValid) Destinations.HOME_SCREEN else Destinations.HOME_SCREEN
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Destinations.SPLASH_SCREEN) {
            SplashScreen(
                onTimeout = {
                    navController.popBackStack()
                    navController.navigate(
                        if (startDestination == Destinations.HOME_SCREEN)
                            Destinations.HOME_SCREEN
                        else
                            Destinations.HOME_SCREEN
                    )
                }
            )
        }

        // Onboarding
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(
                onFinishClicked = {
                    navController.popBackStack()
                    navController.navigate(Destinations.OPENING_SCREEN)
                },
                onSkipClicked = {
                    navController.popBackStack()
                    navController.navigate(Destinations.OPENING_SCREEN)
                }
            )
        }

        // Opening Screen
        composable(Destinations.OPENING_SCREEN) {
            OpeningScreen(
                onLoginClicked = { navController.navigate(Destinations.LOGIN_SCREEN) },
                onSignUpClicked = { navController.navigate(Destinations.SIGNUP_SCREEN) }
            )
        }

        // Login Screen
        composable(Destinations.LOGIN_SCREEN) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onSignUpClicked = { navController.navigate(Destinations.SIGNUP_SCREEN) },
                onForgotPasswordClicked = { }
            )
        }

        // SignUp Screen
        composable(Destinations.SIGNUP_SCREEN) {
            val viewModel: AuthViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onLoginFailed = { navController.navigate(Destinations.LOGIN_SCREEN) },
                onLoginClicked = { navController.navigate(Destinations.LOGIN_SCREEN) }
            )
        }

        // Home Screen
        composable(Destinations.HOME_SCREEN) {
            HomeScreen(
                onKuisClicked = { navController.navigate(Destinations.KUIS_SCREEN)},
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.navigate(Destinations.HOME_SCREEN) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        // Kuis
        composable(Destinations.KUIS_SCREEN) {
            KuisScreen(
                navController = navController,
            )
        }

        composable(
            route = Destinations.KUIS_DETAIL_SCREEN + "/{quizId}",
            arguments = listOf(
                navArgument("quizId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            KuisDetailScreen(
                navController = navController,
                quizId = quizId
            )
        }

        // Voice to Texts
        composable(Destinations.VTT_SCREEN) {
            VoiceToTextScreen(
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.navigate(Destinations.HOME_SCREEN) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        // Kamus Screen
        composable(Destinations.KAMUS_SCREEN) {
            KamusScreen(
                navController = navController,
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.navigate(Destinations.HOME_SCREEN) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        // Profile Screen
        composable(Destinations.PROFILE_SCREEN) {
            ProfileScreen(
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.navigate(Destinations.HOME_SCREEN) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        composable(
            route = "kamus_list/{letter}"
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter")?.firstOrNull() ?: 'A'
            KamusListScreen(
                letter = letter,
                navController = navController
            )
        }

        composable(
            route = Destinations.KAMUS_DETAIL_SCREEN + "/{arti}/{videoUrl}",
            arguments = listOf(
                navArgument("arti") { type = NavType.StringType },
                navArgument("videoUrl") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val arti = backStackEntry.arguments?.getString("arti") ?: ""
            val videoUrl = backStackEntry.arguments?.getString("videoUrl")
            KamusDetailScreen(
                navController = navController,
                arti = arti,
                videoUrl = videoUrl
            )
        }

    }
}
