package com.okeyo.fundilink.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.okeyo.fundilink.navigation.ROUTE_ALERTS
import com.okeyo.fundilink.navigation.ROUTE_HOME
import com.okeyo.fundilink.navigation.ROUTE_JOBS
import com.okeyo.fundilink.navigation.ROUTE_PROFILE
import com.okeyo.fundilink.screens.alerts.AlertsScreen
import com.okeyo.fundilink.screens.home.HomeScreen
import com.okeyo.fundilink.screens.jobs.JobsScreen
import com.okeyo.fundilink.screens.profile.ProfileScreen
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkSurface
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean> = mutableStateOf(true)
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {

                NavigationBarItem(
                    selected = currentRoute == ROUTE_HOME,
                    onClick = {
                        bottomNavController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(text = "Home", fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Orange,
                        selectedTextColor = Orange,
                        unselectedIconColor = GrayText,
                        unselectedTextColor = GrayText,
                        indicatorColor = DarkBackground
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == ROUTE_JOBS,
                    onClick = {
                        bottomNavController.navigate(ROUTE_JOBS) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Work, contentDescription = "Jobs") },
                    label = { Text(text = "Jobs", fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Orange,
                        selectedTextColor = Orange,
                        unselectedIconColor = GrayText,
                        unselectedTextColor = GrayText,
                        indicatorColor = DarkBackground
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == ROUTE_ALERTS,
                    onClick = {
                        bottomNavController.navigate(ROUTE_ALERTS) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text(text = "Alerts", fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Orange,
                        selectedTextColor = Orange,
                        unselectedIconColor = GrayText,
                        unselectedTextColor = GrayText,
                        indicatorColor = DarkBackground
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == ROUTE_PROFILE,
                    onClick = {
                        bottomNavController.navigate(ROUTE_PROFILE) {
                            popUpTo(ROUTE_HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(text = "Profile", fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Orange,
                        selectedTextColor = Orange,
                        unselectedIconColor = GrayText,
                        unselectedTextColor = GrayText,
                        indicatorColor = DarkBackground
                    )
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    navController = navController,
                    bottomNavController = bottomNavController
                )
            }
            composable(ROUTE_JOBS) {
                JobsScreen(navController = navController)
            }
            composable(ROUTE_ALERTS) {
                AlertsScreen(navController = navController)
            }
            composable(ROUTE_PROFILE) {
                ProfileScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}