package com.example.signlink

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.signlink.screens.onboarding.OnboardingScreen
import com.example.signlink.ui.theme.SignLinkTeal
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.delay

object Destinations {
    const val SPLASH_SCREEN = "splash_screen"
    const val ONBOARDING = "onboarding_screen"
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
                    navController.navigate(Destinations.HOME_SCREEN)
                },
                onSkipClicked = {
                    navController.popBackStack()
                    navController.navigate(Destinations.HOME_SCREEN)
                }
            )
        }

        composable(Destinations.HOME_SCREEN) {
            HomeScreen()
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000L)
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignLinkTeal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hitam_logo),
            contentDescription = "SignLink Logo",
            modifier = Modifier.size(200.dp)
        )
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