package com.okeyo.fundilink.screens.splash

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.okeyo.fundilink.R
import com.okeyo.fundilink.navigation.ROUTE_LOGIN
import com.okeyo.fundilink.navigation.ROUTE_MAIN
import com.okeyo.fundilink.navigation.ROUTE_ONBOARDING
import com.okeyo.fundilink.navigation.ROUTE_SPLASH
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.Gold
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        val user = FirebaseAuth.getInstance().currentUser
        val prefs = context.getSharedPreferences("fundilink_prefs", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("is_first_time", true)
        when {
            isFirstTime -> {
                navController.navigate(ROUTE_ONBOARDING) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            }
            user != null -> {
                navController.navigate(ROUTE_MAIN) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseAnim)
                        .clip(CircleShape)
                        .background(Orange.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnim)
                        .clip(CircleShape)
                        .background(Orange.copy(alpha = 0.2f))
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "FundiLink Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scaleAnim)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FundiLink",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Orange,
                modifier = Modifier.scale(scaleAnim)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find & Hire Local Artisans",
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = GrayText,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Powered by FundiLink ⚡",
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Gold,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}