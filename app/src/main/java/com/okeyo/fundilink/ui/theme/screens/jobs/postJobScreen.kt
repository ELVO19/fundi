package com.okeyo.fundilink.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.okeyo.fundilink.models.JobModel
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkCard
import com.okeyo.fundilink.ui.theme.DarkSurface
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import com.okeyo.fundilink.ui.theme.RedError
import com.okeyo.fundilink.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavHostController) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("🔧 Plumbing") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val categories = listOf(
        "🔧 Plumbing",
        "⚡ Electrical",
        "🪚 Carpentry",
        "🔨 Welding",
        "🏠 Masonry",
        "🎨 Painting"
    )

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val jobsRef = FirebaseDatabase.getInstance().getReference("jobs")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Post a Job",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Tell fundis what you need 🔧",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = White
            )

            Text(
                text = "Fill in the details and get bids from nearby fundis",
                fontFamily = Poppins,
                fontSize = 13.sp,
                color = GrayText
            )


            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title e.g Fix leaking pipe", fontFamily = Poppins) },
                leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, tint = Orange) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )


            Text(
                text = "Category",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = White
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedCategory == category) Orange else DarkCard
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontFamily = Poppins,
                            fontSize = 13.sp,
                            color = White,
                            fontWeight = if (selectedCategory == category)
                                FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }


            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontFamily = Poppins) },
                leadingIcon = { Icon(Icons.Default.Create, contentDescription = null, tint = Orange) },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )


            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location e.g Westlands, Nairobi", fontFamily = Poppins) },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Orange) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )


            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget (Ksh)", fontFamily = Poppins) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
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
                    when {
                        title.isEmpty() || description.isEmpty()
                                || location.isEmpty() || budget.isEmpty() -> {
                            errorMessage = "Please fill in all fields"
                        }
                        else -> {
                            isLoading = true
                            errorMessage = ""
                            val jobId = jobsRef.push().key ?: ""
                            val createdAt = SimpleDateFormat(
                                "dd MMM yyyy, hh:mm a",
                                Locale.getDefault()
                            ).format(Date())
                            val job = JobModel(
                                id = jobId,
                                clientId = uid,
                                title = title,
                                description = description,
                                location = location,
                                budget = budget,
                                category = selectedCategory,
                                status = "open",
                                createdAt = createdAt
                            )
                            jobsRef.child(jobId).setValue(job)
                                .addOnSuccessListener {
                                    isLoading = false
                                    navController.popBackStack()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    errorMessage = e.message ?: "Failed to post job"
                                }
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
                        text = "Post Job",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PostJobScreenPreview() {
    PostJobScreen(rememberNavController())
}