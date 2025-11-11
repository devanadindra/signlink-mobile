package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.ui.theme.SignLinkTeal

@Composable
fun OpeningScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(150.dp))

        Image(
            painter = painterResource(id = R.drawable.signlink),
            contentDescription = "SignLink Logo",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Masuk",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignUpClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(2.dp, SignLinkTeal, RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = SignLinkTeal
            ),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Buat Akun",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SignLinkTeal
            )
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}