package com.example.signlink

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.signlink.screens.onboarding.OnboardingScreen
import com.example.signlink.screens.SplashScreen
import com.example.signlink.screens.OpeningScreen
import com.example.signlink.screens.auth.LoginScreen
import com.example.signlink.screens.auth.SignUpScreen
import com.google.accompanist.navigation.animation.composable
import com.example.signlink.viewmodel.AuthViewModel

object Destinations {
    const val SPLASH_SCREEN = "splash_screen"
    const val ONBOARDING = "onboarding_screen"
    const val OPENING_SCREEN = "opening_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val SIGNUP_SCREEN = "signup_screen"
    const val HOME_SCREEN = "home_screen"
}

@OptIn(ExperimentalAnimationApi::class)
@Suppress("DEPRECATION")
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.SPLASH_SCREEN
    ) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashScreen(
                onTimeout = {
                    navController.popBackStack()
                    navController.navigate(Destinations.ONBOARDING)
                }
            )
        }

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

        composable(Destinations.OPENING_SCREEN) {
            OpeningScreen(
                onLoginClicked = {navController.navigate(Destinations.LOGIN_SCREEN)},
                onSignUpClicked = {navController.navigate(Destinations.SIGNUP_SCREEN)}
            )
        }
        composable(Destinations.LOGIN_SCREEN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onSignUpClicked = { navController.navigate(Destinations.SIGNUP_SCREEN) },
                onForgotPasswordClicked = { }
            )
        }

        composable(Destinations.SIGNUP_SCREEN) {
            val viewModel: AuthViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.popBackStack(Destinations.OPENING_SCREEN, inclusive = true)
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onLoginClicked = { navController.navigate(Destinations.LOGIN_SCREEN) }
            )
        }

        composable(Destinations.HOME_SCREEN) {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen (Selamat Datang)")
    }
}