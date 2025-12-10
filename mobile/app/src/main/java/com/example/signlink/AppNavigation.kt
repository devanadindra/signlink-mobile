package com.example.signlink

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.signlink.data.models.kamus.KamusData
import com.example.signlink.screens.HomeScreen
import com.example.signlink.screens.kamus.KamusScreen
import com.example.signlink.screens.kamus.KamusListScreen
import com.example.signlink.screens.kamus.KamusDetailScreen
import com.example.signlink.screens.kamus.AddKamusScreen
import com.example.signlink.screens.profile.ProfileScreen
import com.example.signlink.screens.profile.EditProfileScreen
import com.example.signlink.screens.VoiceToTextScreen
import com.example.signlink.screens.SplashScreen
import com.example.signlink.screens.OpeningScreen
import com.example.signlink.screens.SignClassifierScreen
import com.example.signlink.screens.auth.ChangePasswordScreen
import com.example.signlink.screens.auth.ForgotPasswordScreen
import com.example.signlink.screens.onboarding.OnboardingScreen
import com.example.signlink.screens.auth.LoginScreen
import com.example.signlink.screens.auth.ResetPasswordSubmitScreen
import com.example.signlink.screens.auth.SignUpScreen
import com.example.signlink.screens.kuis.KuisScreen
import com.example.signlink.screens.kuis.KuisDetailScreen
import com.example.signlink.screens.kuis.KuisResultScreen
import com.example.signlink.screens.latihan.AddLatihanScreen
import com.example.signlink.screens.latihan.LatihanScreen
import com.example.signlink.screens.latihan.LatihanDetailScreen
import com.example.signlink.screens.latihan.LatihanResultScreen
import com.example.signlink.screens.latihan.StatsLatihanScreen
import com.example.signlink.screens.profile.HelpScreen
import com.example.signlink.screens.profile.PhotoProfileScreen
import com.example.signlink.screens.profile.PrivacyPolicyScreen
import com.example.signlink.screens.tti.TTIResultScreen
import com.example.signlink.screens.tti.TextToIsyaratScreen
import com.example.signlink.viewmodel.AuthViewModel
import com.example.signlink.viewmodel.CustomerViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.json.JSONObject

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
    const val TTI_SCREEN = "tti_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val PHOTO_PROFILE_SCREEN = "photo_profile_screen"
    const val EDIT_PROFILE_SCREEN = "edit_profile_screen"
    const val LATIHAN_SCREEN = "latihan_screen"
    const val LATIHAN_DETAIL_SCREEN = "latihan_detail_screen/{charactersJson}"
    const val ADD_LATIHAN_SCREEN = "add_latihan_screen"
    const val LATIHAN_RESULT_SCREEN = "latihan_result_screen"
    const val STATS_LATIHAN_SCREEN = "stats_latihan_screen"
    const val SIGN_CLASSIFIER_SCREEN = "sign_classifier_screen"
    const val FORGOT_PASSWORD_SCREEN = "forgot_password_screen"
    const val CHANGE_PASSWORD_SCREEN = "change_password_screen"
    const val PRIVACY_POLICY_SCREEN = "privacy_policy_screen"
    const val HELP_SCREEN = "help_screen"
}

@OptIn(ExperimentalAnimationApi::class)
@Suppress("DEPRECATION")
@Composable
fun AppNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val customerViewModel: CustomerViewModel = hiltViewModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf(Destinations.SPLASH_SCREEN) }
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            authViewModel.handleGoogleSignInResult(context, result) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(context, "Berhasil: Selamat Datang 👋🏻", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                }
            }
        }

    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.White

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = true
        )
    }

    LaunchedEffect(Unit) {
        authViewModel.checkJwt(context) { isValid ->
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
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onSignUpClicked = { navController.navigate(Destinations.SIGNUP_SCREEN) },
                onForgotPasswordClicked = { navController.navigate(Destinations.FORGOT_PASSWORD_SCREEN) },
                onGoogleAuth = {
                    val client = authViewModel.getGoogleSignInClient(context)
                    googleSignInLauncher.launch(client.signInIntent)
                }
            )
        }

        // SignUp Screen
        composable(Destinations.SIGNUP_SCREEN) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onLoginFailed = { navController.navigate(Destinations.LOGIN_SCREEN) },
                onLoginClicked = { navController.navigate(Destinations.LOGIN_SCREEN) },
                onGoogleAuth = {
                    val client = authViewModel.getGoogleSignInClient(context)
                    googleSignInLauncher.launch(client.signInIntent)
                }
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
                onTTIClicked = { navController.navigate(Destinations.TTI_SCREEN)} ,
                onHomeClicked = { navController.popBackStack() },
                onProfileClicked = { navController.navigate(Destinations.PROFILE_SCREEN) },
                customerViewModel = customerViewModel
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
            ForgotPasswordScreen(
                viewModel = authViewModel,
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
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "CUSTOMER"

            ResetPasswordSubmitScreen(
                viewModel = authViewModel,
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
                onAddLatihanClicked = { navController.navigate(Destinations.ADD_LATIHAN_SCREEN) }
            )
        }

        composable(
            route = "${Destinations.LATIHAN_DETAIL_SCREEN}/{latihanId}",
            arguments = listOf(
                navArgument("latihanId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val latihanId = backStackEntry.arguments?.getString("latihanId") ?: ""
            LatihanDetailScreen(navController, latihanId)
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

        // Add Kamus Screen
        composable(Destinations.ADD_LATIHAN_SCREEN) {
            AddLatihanScreen(
                navController = navController,
            )
        }

        composable(route = Destinations.CHANGE_PASSWORD_SCREEN + "/{hasPassword}") { backStackEntry ->
            val hasPassword = backStackEntry.arguments?.getString("hasPassword")?.toBoolean() == true
            ChangePasswordScreen(
                viewModel = authViewModel,
                navController = navController,
                hasPassword = hasPassword,
                onChangePasswordSuccess = { navController.navigate(Destinations.PROFILE_SCREEN) }
            )
        }

        // Texts to Isyarat
        composable(Destinations.TTI_SCREEN) {
            TextToIsyaratScreen(
                navController = navController,
            )
        }

        // Privacy Policy
        composable(Destinations.PRIVACY_POLICY_SCREEN) {
            PrivacyPolicyScreen(
                navController = navController,
            )
        }

        // Help
        composable(Destinations.HELP_SCREEN) {
            HelpScreen(
                navController = navController,
            )
        }

        composable("tti_result_screen") { backStackEntry ->
            val kamusData = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<KamusData>>("kamus_items") ?: emptyList()

            TTIResultScreen(
                navController = navController,
                data = kamusData
            )
        }

        // edit profile
        composable(
            route = "${Destinations.EDIT_PROFILE_SCREEN}/{data}",
            arguments = listOf(
                navArgument("data") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val encoded = backStackEntry.arguments?.getString("data") ?: ""
            val decodedJson = Uri.decode(encoded)

            val json = JSONObject(decodedJson)

            val name = json.getString("name")
            val email = json.getString("email")
            val googleID = json.getString("google_id")

            EditProfileScreen(
                customerViewModel = customerViewModel,
                navController = navController,
                initialName = name,
                initialEmail = email,
                initialGoogleID = googleID
            )
        }

        // Stats Latihan
        composable(Destinations.STATS_LATIHAN_SCREEN) {
            StatsLatihanScreen(
                navController = navController,
            )
        }

        // Foto Profil
        composable( route = Destinations.PHOTO_PROFILE_SCREEN ) {
            PhotoProfileScreen(
                navController = navController,
                customerViewModel= customerViewModel
            )
        }
    }
}
