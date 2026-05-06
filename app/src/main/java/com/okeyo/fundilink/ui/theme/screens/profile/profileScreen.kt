package com.okeyo.fundilink.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.okeyo.fundilink.data.AuthViewModel
import com.okeyo.fundilink.data.CloudinaryHelper
import com.okeyo.fundilink.models.UserModel
import com.okeyo.fundilink.navigation.ROUTE_LOGIN
import com.okeyo.fundilink.navigation.ROUTE_MAIN
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
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel() ,
    isDarkTheme: MutableState<Boolean> = mutableStateOf(true)
) {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf<UserModel?>(null) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploadingPhoto = true
            uploadMessage = ""
            CloudinaryHelper.uploadImage(
                context = context,
                imageUri = it,
                onSuccess = { imageUrl ->
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(uid)
                        .child("profileImage")
                        .setValue(imageUrl)
                        .addOnSuccessListener {
                            isUploadingPhoto = false
                            uploadMessage = "✅ Photo updated!"
                            authViewModel.getCurrentUser(
                                onSuccess = { user -> currentUser = user },
                                onError = {}
                            )
                        }
                },
                onError = { error ->
                    isUploadingPhoto = false
                    uploadMessage = "❌ Upload failed: $error"
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser(
            onSuccess = { currentUser = it },
            onError = {}
        )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))


            Box(contentAlignment = Alignment.BottomEnd) {
                if (currentUser?.profileImage?.isNotEmpty() == true) {
                    AsyncImage(
                        model = currentUser?.profileImage,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Orange)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.first()?.uppercase() ?: "U",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = White
                        )
                    }
                }


                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Orange)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            if (isUploadingPhoto) {
                CircularProgressIndicator(color = Orange, modifier = Modifier.size(24.dp))
                Text(
                    text = "Uploading photo...",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = GrayText
                )
            }

            if (uploadMessage.isNotEmpty()) {
                Text(
                    text = uploadMessage,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = GreenSuccess
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentUser?.name ?: "Loading...",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Orange.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "User",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Orange,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${currentUser?.rating ?: 0f} Rating",
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Profile Information",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Orange
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(
                        icon = { Icon(Icons.Default.Email, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp)) },
                        label = "Email",
                        value = currentUser?.email ?: "—"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(
                        icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp)) },
                        label = "Phone",
                        value = currentUser?.phone ?: "—"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(
                        icon = { Icon(Icons.Default.Place, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp)) },
                        label = "Location",
                        value = currentUser?.location ?: "—"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(
                        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp)) },
                        label = "Role",
                        value = currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "—"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_MAIN) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError)
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontFamily = Poppins, fontSize = 11.sp, color = GrayText)
            Text(text = value, fontFamily = Poppins, fontSize = 14.sp, color = White, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}