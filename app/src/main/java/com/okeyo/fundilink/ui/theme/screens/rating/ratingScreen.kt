package com.okeyo.fundilink.screens.rating


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.okeyo.fundilink.models.ReviewModel
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkSurface
import com.okeyo.fundilink.ui.theme.Gold
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import com.okeyo.fundilink.ui.theme.RedError
import com.okeyo.fundilink.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateFundiScreen(
    navController: NavHostController,
    fundiId: String,
    jobId: String
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rate Fundi",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "⭐", fontSize = 72.sp)

            Text(
                text = "Rate the Fundi",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = White
            )

            Text(
                text = "How was the job done?",
                fontFamily = Poppins,
                fontSize = 13.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $star",
                        tint = if (star <= rating) Gold else GrayText,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = star }
                    )
                }
            }

            Text(
                text = when (rating) {
                    1 -> "Poor 😞"
                    2 -> "Fair 😐"
                    3 -> "Good 🙂"
                    4 -> "Very Good 😊"
                    5 -> "Excellent 🤩"
                    else -> "Tap a star to rate"
                },
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (rating > 0) Gold else GrayText
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Write a review (optional)", fontFamily = Poppins) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontFamily = Poppins,
                    color = RedError,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    if (rating == 0) {
                        errorMessage = "Please select a rating"
                    } else {
                        isLoading = true
                        errorMessage = ""
                        val reviewId = reviewsRef.push().key ?: ""
                        val review = ReviewModel(
                            id = reviewId,
                            fundiId = fundiId,
                            clientId = uid,
                            jobId = jobId,
                            rating = rating.toFloat(),
                            comment = comment
                        )

                        reviewsRef.child(reviewId).setValue(review)
                        usersRef.child(fundiId).child("rating")
                            .setValue(rating.toFloat())
                            .addOnSuccessListener {
                                isLoading = false
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                isLoading = false
                                navController.popBackStack()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Submit Review ⭐",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RateFundiScreenPreview() {
    RateFundiScreen(rememberNavController(), fundiId = "", jobId = "")
}