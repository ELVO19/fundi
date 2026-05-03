package com.okeyo.fundilink.screens.admin

import android.graphics.Color
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.okeyo.fundilink.data.AuthViewModel
import com.okeyo.fundilink.models.JobModel
import com.okeyo.fundilink.models.UserModel
import com.okeyo.fundilink.navigation.ROUTE_ADMIN_DASHBOARD
import com.okeyo.fundilink.navigation.ROUTE_LOGIN
import com.okeyo.fundilink.navigation.ROUTE_MANAGE_USERS
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {

    var totalUsers by remember { mutableStateOf(0) }
    var totalFundis by remember { mutableStateOf(0) }
    var totalClients by remember { mutableStateOf(0) }
    var totalJobs by remember { mutableStateOf(0) }
    var openJobs by remember { mutableStateOf(0) }
    var closedJobs by remember { mutableStateOf(0) }
    var completedJobs by remember { mutableStateOf(0) }
    var totalBids by remember { mutableStateOf(0) }
    var recentUsers by remember { mutableStateOf<List<UserModel>>(emptyList()) }

    LaunchedEffect(Unit) {

        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<UserModel>()
                    snapshot.children.forEach {
                        val user = it.getValue(UserModel::class.java)
                        if (user != null) users.add(user)
                    }
                    totalUsers = users.size
                    totalFundis = users.count { it.role == "fundi" }
                    totalClients = users.count { it.role == "client" }
                    recentUsers = users.takeLast(5).reversed()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        FirebaseDatabase.getInstance().getReference("jobs")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = mutableListOf<JobModel>()
                    snapshot.children.forEach {
                        val job = it.getValue(JobModel::class.java)
                        if (job != null) jobs.add(job)
                    }
                    totalJobs = jobs.size
                    openJobs = jobs.count { it.status == "open" }
                    closedJobs = jobs.count { it.status == "closed" }
                    completedJobs = jobs.count { it.status == "completed" }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        FirebaseDatabase.getInstance().getReference("bids")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalBids = snapshot.childrenCount.toInt()
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
                        text = "Admin Dashboard",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Orange
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_MANAGE_USERS)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Manage Users",
                            tint = White
                        )
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(ROUTE_ADMIN_DASHBOARD) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = RedError
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Users Stats
            item {
                Text(
                    text = "Users Overview",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Users",
                        value = totalUsers.toString(),
                        color = Orange
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Fundis",
                        value = totalFundis.toString(),
                        color = Gold
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Clients",
                        value = totalClients.toString(),
                        color = GreenSuccess
                    )
                }
            }

            // Jobs Stats
            item {
                Text(
                    text = "Jobs Overview",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Jobs",
                        value = totalJobs.toString(),
                        color = Orange
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Open",
                        value = openJobs.toString(),
                        color = GreenSuccess
                    )
                    AdminStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Bids",
                        value = totalBids.toString(),
                        color = Gold
                    )
                }
            }

            // PIE CHART — Users Distribution
            item {
                Text(
                    text = "Users Distribution 📊",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    AndroidView(
                        factory = { context ->
                            PieChart(context).apply {
                                description.isEnabled = false
                                isDrawHoleEnabled = true
                                holeRadius = 40f
                                setHoleColor(Color.TRANSPARENT)
                                setTransparentCircleAlpha(0)
                                setDrawEntryLabels(false)
                                legend.apply {
                                    isEnabled = true
                                    textColor = Color.WHITE
                                    textSize = 12f
                                    form = Legend.LegendForm.CIRCLE
                                }
                                setBackgroundColor(Color.TRANSPARENT)
                            }
                        },
                        update = { chart ->
                            val entries = listOf(
                                PieEntry(totalFundis.toFloat(), "Fundis"),
                                PieEntry(totalClients.toFloat(), "Clients")
                            )
                            val dataSet = PieDataSet(entries, "").apply {
                                colors = listOf(
                                    android.graphics.Color.parseColor("#FF6B00"),
                                    android.graphics.Color.parseColor("#4CAF50")
                                )
                                valueTextColor = Color.WHITE
                                valueTextSize = 14f
                            }
                            chart.data = PieData(dataSet)
                            chart.invalidate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(16.dp)
                    )
                }
            }

            // BAR CHART — Jobs Status
            item {
                Text(
                    text = "Jobs Status 📈",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    AndroidView(
                        factory = { context ->
                            BarChart(context).apply {
                                description.isEnabled = false
                                setDrawGridBackground(false)
                                setBackgroundColor(Color.TRANSPARENT)
                                xAxis.apply {
                                    textColor = Color.WHITE
                                    granularity = 1f
                                    setDrawGridLines(false)
                                }
                                axisLeft.apply {
                                    textColor = Color.WHITE
                                    setDrawGridLines(false)
                                    granularity = 1f
                                }
                                axisRight.isEnabled = false
                                legend.apply {
                                    isEnabled = false
                                }
                                setTouchEnabled(false)
                            }
                        },
                        update = { chart ->
                            val entries = listOf(
                                BarEntry(0f, openJobs.toFloat()),
                                BarEntry(1f, closedJobs.toFloat()),
                                BarEntry(2f, completedJobs.toFloat()),
                                BarEntry(3f, totalBids.toFloat())
                            )
                            val dataSet = BarDataSet(entries, "").apply {
                                colors = listOf(
                                    android.graphics.Color.parseColor("#4CAF50"),
                                    android.graphics.Color.parseColor("#E53935"),
                                    android.graphics.Color.parseColor("#FFD700"),
                                    android.graphics.Color.parseColor("#FF6B00")
                                )
                                valueTextColor = Color.WHITE
                                valueTextSize = 12f
                            }
                            chart.xAxis.valueFormatter = IndexAxisValueFormatter(
                                listOf("Open", "Closed", "Done", "Bids")
                            )
                            chart.data = BarData(dataSet)
                            chart.invalidate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(16.dp)
                    )
                }
            }

            // Recent Users
            item {
                Text(
                    text = "Recent Users",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
            }

            items(recentUsers.size) { index ->
                val user = recentUsers[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Orange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.first().uppercase(),
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    color = White,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = White
                                )
                                Text(
                                    text = user.email,
                                    fontFamily = Poppins,
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (user.role == "fundi") Gold.copy(alpha = 0.2f)
                                    else GreenSuccess.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = user.role.replaceFirstChar { it.uppercase() },
                                fontFamily = Poppins,
                                fontSize = 11.sp,
                                color = if (user.role == "fundi") Gold else GreenSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = color
            )
            Text(
                text = title,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = GrayText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    AdminDashboard(rememberNavController())
}