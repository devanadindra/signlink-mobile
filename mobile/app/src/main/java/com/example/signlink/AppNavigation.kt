package com.example.signlink

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
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
import com.example.signlink.screens.kamus.AddKamusScreen
import com.example.signlink.screens.ProfileScreen
import com.example.signlink.screens.VoiceToTextScreen
import com.example.signlink.screens.SplashScreen
import com.example.signlink.screens.OpeningScreen
import com.example.signlink.screens.SignClassifierScreen
import com.example.signlink.screens.auth.ForgotPasswordScreen
import com.example.signlink.screens.onboarding.OnboardingScreen
import com.example.signlink.screens.auth.LoginScreen
import com.example.signlink.screens.auth.ResetPasswordSubmitScreen
import com.example.signlink.screens.auth.SignUpScreen
import com.example.signlink.screens.kuis.KuisScreen
import com.example.signlink.screens.kuis.KuisDetailScreen
import com.example.signlink.screens.kuis.KuisResultScreen
import com.example.signlink.screens.latihan.LatihanScreen
import com.example.signlink.screens.latihan.LatihanDetailScreen
import com.example.signlink.screens.latihan.LatihanResultScreen
import com.example.signlink.viewmodel.AuthViewModel
import com.example.signlink.viewmodel.CustomerViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

object Destinations {
    const val SPLASH_SCREEN = "splash_screen"
    const val ONBOARDING = "onboarding_screen"
    const val OPENING_SCREEN = "opening_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val SIGNUP_SCREEN = "signup_screen"
    const val HOME_SCREEN = "home_screen"
    const val VTT_SCREEN = "vtt_screen"
    const val KAMUS_SCREEN = "kamus_screen"
    const val ADD_KAMUS_SCREEN = "add_kamus_screen"
    const val KUIS_SCREEN = "kuis_screen"
    const val KUIS_DETAIL_SCREEN = "kuis_detail_screen"
    const val KUIS_RESULT_SCREEN = "kuis_result_screen"
    const val KAMUS_DETAIL_SCREEN = "kamus_detail_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val LATIHAN_SCREEN = "latihan_screen"
    const val LATIHAN_DETAIL_SCREEN = "latihan_detail_screen/{charactersJson}"
    const val LATIHAN_RESULT_SCREEN = "latihan_result_screen"
    const val SIGN_CLASSIFIER_SCREEN = "sign_classifier_screen"
    const val FORGOT_PASSWORD_SCREEN = "forgot_password_screen"
}

@OptIn(ExperimentalAnimationApi::class)
@Suppress("DEPRECATION")
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf(Destinations.SPLASH_SCREEN) }
    val viewModel: AuthViewModel = hiltViewModel()

    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.White

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = true
        )
    }

    LaunchedEffect(Unit) {
        viewModel.checkJwt(context) { isValid ->
            startDestination = if (isValid) Destinations.HOME_SCREEN else Destinations.ONBOARDING
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
                            Destinations.ONBOARDING
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
                onForgotPasswordClicked = { navController.navigate(Destinations.FORGOT_PASSWORD_SCREEN) }
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
                onCameraClicked = { navController.navigate(Destinations.SIGN_CLASSIFIER_SCREEN)},
                onKuisClicked = { navController.navigate(Destinations.KUIS_SCREEN)},
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onLatihanClicked = { navController.navigate(Destinations.LATIHAN_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.popBackStack() },
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

        composable(
            route = Destinations.KUIS_RESULT_SCREEN + "/{quizId}",
            arguments = listOf(
                navArgument("quizId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            KuisResultScreen(
                navController = navController,
                quizId = quizId
            )
        }

        // Voice to Texts
        composable(Destinations.VTT_SCREEN) {
            VoiceToTextScreen(
                onCameraClicked = { navController.navigate(Destinations.SIGN_CLASSIFIER_SCREEN)},
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.popBackStack() },
                onHomeClicked = { navController.popBackStack(Destinations.HOME_SCREEN, inclusive = false) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        // Kamus Screen
        composable(Destinations.KAMUS_SCREEN) {
            KamusScreen(
                navController = navController,
                onCameraClicked = { navController.navigate(Destinations.SIGN_CLASSIFIER_SCREEN)},
                onKamusClicked = { navController.popBackStack() },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.popBackStack(Destinations.HOME_SCREEN, inclusive = false) },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) },
                onAddKamusClicked = { navController.navigate(Destinations.ADD_KAMUS_SCREEN) }
            )
        }

        // Profile Screen
        composable(Destinations.PROFILE_SCREEN) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val customerViewModel: CustomerViewModel = hiltViewModel()
            ProfileScreen(
                navController = navController,
                viewModel = authViewModel,
                customerViewModel = customerViewModel,
                onCameraClicked = { navController.navigate(Destinations.SIGN_CLASSIFIER_SCREEN) },
                onKamusClicked = { navController.navigate(Destinations.KAMUS_SCREEN) },
                onVTTClicked = { navController.navigate(Destinations.VTT_SCREEN) },
                onHomeClicked = { navController.popBackStack(Destinations.HOME_SCREEN, inclusive = false) },
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

        composable(Destinations.SIGN_CLASSIFIER_SCREEN) {
            SignClassifierScreen(
                navController = navController,
            )
        }

        // Add Kamus Screen
        composable(Destinations.ADD_KAMUS_SCREEN) {
            AddKamusScreen(
                navController = navController,
            )
        }

        // Forgot Password Req
        composable(Destinations.FORGOT_PASSWORD_SCREEN) {
            val viewModel: AuthViewModel = hiltViewModel()
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBackToLogin = { navController.popBackStack() },
                onResetEmailSent = { email, role ->
                    navController.navigate("reset_password_submit/$email/$role")
                }
            )
        }

        // Reset Password Submit
        composable(
            route = "reset_password_submit/{email}/{role}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: AuthViewModel = hiltViewModel()
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "CUSTOMER"

            ResetPasswordSubmitScreen(
                viewModel = viewModel,
                email = email,
                role = role,
                onPasswordResetSuccess = {
                    navController.popBackStack(Destinations.LOGIN_SCREEN, inclusive = false)
                }
            )
        }

        // Latihan
        composable(Destinations.LATIHAN_SCREEN) {
            LatihanScreen(
                navController = navController,
            )
        }

        composable(
            route = "${Destinations.LATIHAN_DETAIL_SCREEN}/{charactersJson}",
            arguments = listOf(
                navArgument("charactersJson") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val charactersJson = backStackEntry.arguments?.getString("charactersJson") ?: ""
            LatihanDetailScreen(navController, charactersJson)
        }

        composable(
            route = "${Destinations.LATIHAN_RESULT_SCREEN}/{resultJson}",
            arguments = listOf(
                navArgument("resultJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val resultJson = backStackEntry.arguments?.getString("resultJson") ?: ""

            LatihanResultScreen(
                navController = navController,
                resultJson = resultJson
            )
        }

    }
}
