package com.okeyo.fundilink.screens.onboarding

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.okeyo.fundilink.navigation.ROUTE_LOGIN
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkCard
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import com.okeyo.fundilink.ui.theme.White

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun OnboardingScreen(navController: NavHostController) {

    val context = LocalContext.current

    val pages = listOf(
        OnboardingPage(
            emoji = "🔧",
            title = "Welcome to FundiLink",
            description = "Your one-stop platform for finding and hiring skilled local artisans for any job big or small.",
            color = Orange
        ),
        OnboardingPage(
            emoji = "💼",
            title = "Post Jobs Easily",
            description = "Post your job in seconds. Describe what you need, set your budget and location — fundis will bid for your job!",
            color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
        ),
        OnboardingPage(
            emoji = "⭐",
            title = "Hire the Best Fundi",
            description = "Compare bids, check ratings and reviews, chat with fundis and hire the best one for your job. It's that simple!",
            color = androidx.compose.ui.graphics.Color(0xFFFFD700)
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Skip Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "Skip",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = GrayText,
                    modifier = Modifier.clickable {
                        // Save first time flag
                        context.getSharedPreferences("fundilink_prefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("is_first_time", false).apply()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Emoji
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(pages[currentPage].color.copy(alpha = 0.15f))
                    .alpha(alphaAnim),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pages[currentPage].emoji,
                    fontSize = 72.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = pages[currentPage].title,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = pages[currentPage].description,
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = GrayText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index == currentPage) pages[currentPage].color
                                else DarkCard
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Next/Get Started Button
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        context.getSharedPreferences("fundilink_prefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("is_first_time", false).apply()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pages[currentPage].color
                )
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "Next →" else "Get Started 🚀",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(rememberNavController())
}