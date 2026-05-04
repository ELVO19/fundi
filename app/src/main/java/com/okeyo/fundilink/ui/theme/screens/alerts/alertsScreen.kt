package com.okeyo.fundilink.screens.alerts

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.okeyo.fundilink.models.BidModel
import com.okeyo.fundilink.models.JobModel
import com.okeyo.fundilink.models.UserModel
import com.okeyo.fundilink.navigation.ROUTE_JOB_BIDS
import com.okeyo.fundilink.navigation.ROUTE_RATE_FUNDI
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
import com.okeyo.fundilink.ui.theme.YellowPending

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(navController: NavHostController) {

    var currentUserRole by remember { mutableStateOf("") }
    var myBids by remember { mutableStateOf<List<BidModel>>(emptyList()) }
    var myJobBids by remember { mutableStateOf<List<BidModel>>(emptyList()) }
    var myJobs by remember { mutableStateOf<List<JobModel>>(emptyList()) }
    var fundiNames by remember { mutableStateOf<Map<String, UserModel>>(emptyMap()) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {

        // Get role
        FirebaseDatabase.getInstance().getReference("users")
            .child(uid).child("role").get()
            .addOnSuccessListener { snapshot ->
                currentUserRole = snapshot.value?.toString() ?: ""
            }

        // Fundi bids
        FirebaseDatabase.getInstance().getReference("bids")
            .orderByChild("fundiId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bids = mutableListOf<BidModel>()
                    snapshot.children.forEach {
                        val bid = it.getValue(BidModel::class.java)
                        if (bid != null) bids.add(bid)
                    }
                    myBids = bids
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Client jobs and their bids
        FirebaseDatabase.getInstance().getReference("jobs")
            .orderByChild("clientId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = mutableListOf<JobModel>()
                    snapshot.children.forEach {
                        val job = it.getValue(JobModel::class.java)
                        if (job != null) jobs.add(job)
                    }
                    myJobs = jobs

                    // Fetch bids for client's jobs
                    jobs.forEach { job ->
                        FirebaseDatabase.getInstance().getReference("bids")
                            .orderByChild("jobId").equalTo(job.id)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val bids = mutableListOf<BidModel>()
                                    snapshot.children.forEach {
                                        val bid = it.getValue(BidModel::class.java)
                                        if (bid != null) bids.add(bid)
                                    }
                                    myJobBids = (myJobBids + bids).distinctBy { it.id }

                                    // Fetch fundi names
                                    bids.forEach { bid ->
                                        FirebaseDatabase.getInstance().getReference("users")
                                            .child(bid.fundiId).get()
                                            .addOnSuccessListener { userSnapshot ->
                                                val user = userSnapshot.getValue(UserModel::class.java)
                                                if (user != null) {
                                                    fundiNames = fundiNames + (bid.fundiId to user)
                                                }
                                            }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
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
                        text = "FundiLink",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Orange
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ============ CLIENT ALERTS ============
            if (currentUserRole == "client") {
                item {
                    Text(
                        text = "Bids on My Jobs 📋",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${myJobBids.size} bid${if (myJobBids.size != 1) "s" else ""} received",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }

                if (myJobBids.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "📋", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "No bids received yet", fontFamily = Poppins, color = GrayText, fontSize = 14.sp)
                                Text(text = "Post a job to receive bids!", fontFamily = Poppins, color = GrayText, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(myJobBids) { bid ->
                        val fundi = fundiNames[bid.fundiId]
                        val job = myJobs.find { it.id == bid.jobId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {


                                Text(
                                    text = "📌 ${job?.title ?: "Job"}",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Orange
                                )

                                Spacer(modifier = Modifier.height(8.dp))


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "👷 ${fundi?.name ?: "Fundi"}",
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = White
                                        )
                                        Text(
                                            text = "📍 ${fundi?.location ?: "—"}",
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            color = GrayText
                                        )
                                        Text(
                                            text = "⭐ ${fundi?.rating ?: 0f} Rating",
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            color = Gold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (bid.status) {
                                                    "accepted" -> GreenSuccess.copy(alpha = 0.2f)
                                                    "rejected" -> RedError.copy(alpha = 0.2f)
                                                    else -> YellowPending.copy(alpha = 0.2f)
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = bid.status.replaceFirstChar { it.uppercase() },
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (bid.status) {
                                                "accepted" -> GreenSuccess
                                                "rejected" -> RedError
                                                else -> YellowPending
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "💬 ${bid.message}",
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = GrayText,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "💰 Ksh ${bid.amount}",
                                    fontFamily = Poppins,
                                    fontSize = 13.sp,
                                    color = Gold,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // View All Bids
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Orange.copy(alpha = 0.15f))
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                navController.navigate(
                                                    ROUTE_JOB_BIDS.replace("{jobId}", bid.jobId)
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "View All Bids",
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            color = Orange,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }


                                    if (bid.status == "accepted") {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Gold.copy(alpha = 0.15f))
                                                .padding(vertical = 8.dp)
                                                .clickable {
                                                    navController.navigate(
                                                        ROUTE_RATE_FUNDI
                                                            .replace("{fundiId}", bid.fundiId)
                                                            .replace("{jobId}", bid.jobId)
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "⭐ Rate Fundi",
                                                fontFamily = Poppins,
                                                fontSize = 11.sp,
                                                color = Gold,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (currentUserRole == "fundi") {
                item {
                    Text(
                        text = "My Bids & Status 🔔",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${myBids.size} bid${if (myBids.size != 1) "s" else ""} placed",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }

                if (myBids.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔔", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "No bids placed yet", fontFamily = Poppins, color = GrayText, fontSize = 14.sp)
                                Text(text = "Browse jobs and start bidding!", fontFamily = Poppins, color = GrayText, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(myBids) { bid ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Job #${bid.jobId.take(8)}",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = White
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (bid.status) {
                                                    "accepted" -> GreenSuccess.copy(alpha = 0.2f)
                                                    "rejected" -> RedError.copy(alpha = 0.2f)
                                                    else -> YellowPending.copy(alpha = 0.2f)
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = bid.status.replaceFirstChar { it.uppercase() },
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (bid.status) {
                                                "accepted" -> GreenSuccess
                                                "rejected" -> RedError
                                                else -> YellowPending
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "💬 ${bid.message}", fontFamily = Poppins, fontSize = 12.sp, color = GrayText, maxLines = 2)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "💰 Ksh ${bid.amount}", fontFamily = Poppins, fontSize = 13.sp, color = Gold, fontWeight = FontWeight.SemiBold)

                                // Show accepted message
                                if (bid.status == "accepted") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GreenSuccess.copy(alpha = 0.1f))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "🎉 Congratulations! Your bid was accepted. Contact the client to proceed.",
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            color = GreenSuccess
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlertsScreenPreview() {
    AlertsScreen(rememberNavController())
}