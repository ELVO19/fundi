package com.okeyo.fundilink.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.okeyo.fundilink.models.BidModel
import com.okeyo.fundilink.models.JobModel
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkCard
import com.okeyo.fundilink.ui.theme.DarkSurface
import com.okeyo.fundilink.ui.theme.Gold
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.GreenSuccess
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import com.okeyo.fundilink.ui.theme.RedError
import com.okeyo.fundilink.ui.theme.White
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceBidScreen(
    navController: NavHostController,
    jobId: String
) {
    var job by remember { mutableStateOf<JobModel?>(null) }
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var alreadyBid by remember { mutableStateOf(false) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val bidsRef = FirebaseDatabase.getInstance().getReference("bids")

    LaunchedEffect(jobId) {
        FirebaseDatabase.getInstance().getReference("jobs")
            .child(jobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    job = snapshot.getValue(JobModel::class.java)
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        bidsRef.orderByChild("fundiId").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val bid = it.getValue(BidModel::class.java)
                        if (bid?.jobId == jobId) alreadyBid = true
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Place a Bid",
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            job?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = it.title,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = it.description,
                            fontFamily = Poppins,
                            fontSize = 13.sp,
                            color = GrayText
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "📍 ${it.location}",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = GrayText
                        )
                        Text(
                            text = "💰 Client Budget: Ksh ${it.budget}",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Gold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (alreadyBid) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenSuccess.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "✅ You have already placed a bid on this job",
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = GreenSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "Your Bid Details",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = White
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Your Bid Amount (Ksh)", fontFamily = Poppins) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !alreadyBid
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message to Client", fontFamily = Poppins) },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !alreadyBid
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontFamily = Poppins,
                    color = RedError,
                    fontSize = 12.sp
                )
            }

            if (!alreadyBid) {
                Button(
                    onClick = {
                        when {
                            amount.isEmpty() || message.isEmpty() -> {
                                errorMessage = "Please fill in all fields"
                            }
                            else -> {
                                isLoading = true
                                errorMessage = ""
                                val bidId = bidsRef.push().key ?: ""
                                val bid = BidModel(
                                    id = bidId,
                                    jobId = jobId,
                                    fundiId = uid,
                                    amount = amount,
                                    message = message,
                                    status = "pending"
                                )
                                bidsRef.child(bidId).setValue(bid)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = e.message ?: "Failed to place bid"
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
                            text = "Submit Bid",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaceBidScreenPreview() {
    PlaceBidScreen(rememberNavController(), jobId = "")
}
