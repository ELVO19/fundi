package com.okeyo.fundilink.screens.fundis

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.okeyo.fundilink.models.UserModel
import com.okeyo.fundilink.navigation.ROUTE_CHAT
import com.okeyo.fundilink.screens.shared.FundiCardSkeleton
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
fun SearchFundisScreen(navController: NavHostController) {

    var allFundis by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "🔧 Plumbing", "⚡ Electrical", "🪚 Carpentry", "🔨 Welding", "🏠 Masonry", "🎨 Painting")

    val filteredFundis = allFundis.filter { fundi ->
        (searchQuery.isEmpty() || fundi.name.contains(searchQuery, ignoreCase = true) ||
                fundi.location.contains(searchQuery, ignoreCase = true))
    }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().getReference("users")
            .orderByChild("role").equalTo("fundi")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fundis = mutableListOf<UserModel>()
                    snapshot.children.forEach {
                        val user = it.getValue(UserModel::class.java)
                        if (user != null) fundis.add(user)
                    }
                    allFundis = fundis
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

            item {
                Text(
                    text = "Find a Fundi 👷",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = White
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by name or location...", fontFamily = Poppins) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Orange)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                                fontSize = 12.sp,
                                color = White,
                                fontWeight = if (selectedCategory == category)
                                    FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${filteredFundis.size} fundi${if (filteredFundis.size != 1) "s" else ""} found",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = GrayText
                )
            }

            if (filteredFundis.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No fundis found 👷",
                            fontFamily = Poppins,
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            if (allFundis.isEmpty()) {
                items(5) {
                    FundiCardSkeleton()
                }
            }
            else {
                items(filteredFundis) { fundi ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Orange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fundi.name.first().uppercase(),
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    color = White,
                                    fontSize = 20.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (fundi.isAvailable) GreenSuccess
                                            else RedError
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (fundi.isAvailable) "Available" else "Busy",
                                    fontFamily = Poppins,
                                    fontSize = 10.sp,
                                    color = if (fundi.isAvailable) GreenSuccess else RedError
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fundi.name,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = White
                                )
                                Text(
                                    text = "📍 ${fundi.location}",
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = GrayText
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${fundi.rating} Rating",
                                        fontFamily = Poppins,
                                        fontSize = 11.sp,
                                        color = Gold
                                    )
                                }
                            }


                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GreenSuccess.copy(alpha = 0.15f))
                                    .clickable {
                                        navController.navigate(
                                            ROUTE_CHAT
                                                .replace("{receiverId}", fundi.id)
                                                .replace("{receiverName}", fundi.name)
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
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

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchFundisScreenPreview() {
    SearchFundisScreen(rememberNavController())
}