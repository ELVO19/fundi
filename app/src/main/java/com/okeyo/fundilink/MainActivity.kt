package com.okeyo.fundilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.okeyo.fundilink.data.CloudinaryHelper
import com.okeyo.fundilink.navigation.AppNavHost
import com.okeyo.fundilink.ui.theme.FundiLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CloudinaryHelper.init(this)
        setContent {
            FundiLinkTheme {
                AppNavHost()
            }
        }
    }
}