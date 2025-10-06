@file:Suppress("DEPRECATION")

package com.example.signlink.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.LightText
import com.google.accompanist.pager.HorizontalPager

import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onFinishClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(190.dp))

        HorizontalPager(
            count = onboardingPages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val item = onboardingPages[page]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = item.illustrationId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                )

                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description,
                    fontSize = 16.sp,
                    color = LightText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(onboardingPages.size) { iteration ->
                DotIndicator(isActive = pagerState.currentPage == iteration)
                if (iteration < onboardingPages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))

        val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

        if (isLastPage) {
            Button(
                onClick = onFinishClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 52.dp)
                    .zIndex(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                shape = CircleShape
            ) {
                Text(
                    text = "Yuk Mulai",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 52.dp)
                    .zIndex(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skip",
                    fontSize = 18.sp,
                    color = LightText,
                    modifier = Modifier
                        .clickable(onClick = onSkipClicked)
                )

                Spacer(modifier = Modifier.weight(1f))

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    containerColor = SignLinkTeal,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }

    }
}
