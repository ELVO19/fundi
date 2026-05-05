package com.okeyo.fundilink.screens.jobs

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.okeyo.fundilink.models.BidModel
import com.okeyo.fundilink.models.UserModel
import com.okeyo.fundilink.navigation.ROUTE_RATE_FUNDI
import com.okeyo.fundilink.navigation.ROUTE_CHAT
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
fun JobBidsScreen(
    navController: NavHostController,
    jobId: String
) {
    var bids by remember { mutableStateOf<List<BidModel>>(emptyList()) }
    var fundiNames by remember { mutableStateOf<Map<String, UserModel>>(emptyMap()) }

    val bidsRef = FirebaseDatabase.getInstance().getReference("bids")
    val jobsRef = FirebaseDatabase.getInstance().getReference("jobs")

    LaunchedEffect(jobId) {

        bidsRef.orderByChild("jobId").equalTo(jobId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bidList = mutableListOf<BidModel>()
                    snapshot.children.forEach {
                        val bid = it.getValue(BidModel::class.java)
                        if (bid != null) bidList.add(bid)
                    }
                    bids = bidList


                    bidList.forEach { bid ->
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(bid.fundiId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(UserModel::class.java)
                                    if (user != null) {
                                        fundiNames = fundiNames + (bid.fundiId to user)
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
                        text = "Job Bids",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "${bids.size} Bid${if (bids.size != 1) "s" else ""} Received",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (bids.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No bids received yet",
                                fontFamily = Poppins,
                                color = GrayText,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(bids) { bid ->
                    val fundi = fundiNames[bid.fundiId]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {


                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Orange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = fundi?.name?.first()?.uppercase() ?: "F",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Bold,
                                        color = White,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = fundi?.name ?: "Fundi",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = White
                                    )
                                    Text(
                                        text = "📍 ${fundi?.location ?: "—"}",
                                        fontFamily = Poppins,
                                        fontSize = 11.sp,
                                        color = GrayText
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "⭐ ${fundi?.rating ?: 0f} Rating",
                                            fontFamily = Poppins,
                                            fontSize = 11.sp,
                                            color = Gold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bid Details
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "💬 ${bid.message}",
                                        fontFamily = Poppins,
                                        fontSize = 13.sp,
                                        color = White
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "💰 Bid Amount: Ksh ${bid.amount}",
                                        fontFamily = Poppins,
                                        fontSize = 13.sp,
                                        color = Gold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))


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


                            if (bid.status == "pending") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    OutlinedButton(
                                        onClick = {
                                            bidsRef.child(bid.id)
                                                .child("status")
                                                .setValue("rejected")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "❌ Reject",
                                            fontFamily = Poppins,
                                            color = RedError,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Button(
                                        onClick = {

                                            bidsRef.child(bid.id)
                                                .child("status")
                                                .setValue("accepted")


                                            jobsRef.child(jobId)
                                                .child("status")
                                                .setValue("closed")


                                            bids.filter { it.id != bid.id }.forEach { otherBid ->
                                                bidsRef.child(otherBid.id)
                                                    .child("status")
                                                    .setValue("rejected")
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GreenSuccess
                                        )
                                    ) {
                                        Text(
                                            text = "✅ Accept",
                                            fontFamily = Poppins,
                                            color = White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    if (bid.status == "accepted") {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Gold.copy(alpha = 0.15f))
                                                    .clickable {
                                                        navController.navigate(
                                                            ROUTE_RATE_FUNDI
                                                                .replace("{fundiId}", bid.fundiId)
                                                                .replace("{jobId}", jobId)
                                                        )
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "⭐ Rate Fundi",
                                                    fontFamily = Poppins,
                                                    fontSize = 12.sp,
                                                    color = Gold,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }


                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(GreenSuccess.copy(alpha = 0.15f))
                                                    .clickable {
                                                        navController.navigate(
                                                            ROUTE_CHAT
                                                                .replace("{receiverId}", bid.fundiId)
                                                                .replace("{receiverName}", fundi?.name ?: "Fundi")
                                                        )
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "💬 Chat",
                                                    fontFamily = Poppins,
                                                    fontSize = 12.sp,
                                                    color = GreenSuccess,
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
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JobBidsScreenPreview() {
    JobBidsScreen(rememberNavController(), jobId = "")
}