package com.okeyo.fundilink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.okeyo.fundilink.screens.onboarding.OnboardingScreen
import com.okeyo.fundilink.screens.profile.EditProfileScreen
import com.okeyo.fundilink.screens.auth.ForgotPasswordScreen

import com.okeyo.fundilink.screens.jobs.PlaceBidScreen
import com.okeyo.fundilink.screens.jobs.PostJobScreen
import com.okeyo.fundilink.screens.login.LoginScreen
import com.okeyo.fundilink.screens.main.MainScreen

import com.okeyo.fundilink.screens.register.RegisterScreen
import com.okeyo.fundilink.screens.splash.SplashScreen
import com.okeyo.fundilink.screens.admin.AdminDashboard
import com.okeyo.fundilink.screens.jobs.JobBidsScreen
import com.okeyo.fundilink.ui.theme.screens.admin.ManageUsersScreen
import com.okeyo.fundilink.screens.chat.ChatScreen
import com.okeyo.fundilink.screens.fundis.SearchFundisScreen
import com.okeyo.fundilink.screens.rating.RateFundiScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_SPLASH)          { SplashScreen(navController) }
        composable(ROUTE_LOGIN)           { LoginScreen(navController) }
        composable(ROUTE_REGISTER)        { RegisterScreen(navController) }
        composable(ROUTE_MAIN)            { MainScreen(navController) }
        composable(ROUTE_POST_JOB)        { PostJobScreen(navController) }
        composable(ROUTE_ADMIN_DASHBOARD) { AdminDashboard(navController) }
        composable(ROUTE_MANAGE_USERS)    { ManageUsersScreen(navController) }
        composable(
            route = ROUTE_PLACE_BID,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            PlaceBidScreen(navController = navController, jobId = jobId)


        }
        composable(
            route = ROUTE_JOB_BIDS,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobBidsScreen(navController = navController, jobId = jobId)
        }

                composable(ROUTE_SEARCH_FUNDIS) {
                    SearchFundisScreen(navController = navController)
                }
        composable(
            route = ROUTE_RATE_FUNDI,
            arguments = listOf(
                navArgument("fundiId") { type = NavType.StringType },
                navArgument("jobId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fundiId = backStackEntry.arguments?.getString("fundiId") ?: ""
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            RateFundiScreen(navController = navController, fundiId = fundiId, jobId = jobId)
        }
        composable(
            route = ROUTE_CHAT,
            arguments = listOf(
                navArgument("receiverId") { type = NavType.StringType },
                navArgument("receiverName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""
            ChatScreen(navController = navController, receiverId = receiverId, receiverName = receiverName)
        }

        composable(ROUTE_ONBOARDING) { OnboardingScreen(navController) }
        composable(ROUTE_EDIT_PROFILE) { EditProfileScreen(navController) }
        composable(ROUTE_FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }
    }
}

