package com.okeyo.fundilink.screens.chat

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import com.okeyo.fundilink.models.MessageModel
import com.okeyo.fundilink.ui.theme.DarkBackground
import com.okeyo.fundilink.ui.theme.DarkCard
import com.okeyo.fundilink.ui.theme.DarkSurface
import com.okeyo.fundilink.ui.theme.GrayText
import com.okeyo.fundilink.ui.theme.Orange
import com.okeyo.fundilink.ui.theme.Poppins
import com.okeyo.fundilink.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    receiverId: String,
    receiverName: String
) {
    var messages by remember { mutableStateOf<List<MessageModel>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val chatId = listOf(uid, receiverId).sorted().joinToString("_")
    val chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)

    LaunchedEffect(Unit) {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgs = mutableListOf<MessageModel>()
                snapshot.children.forEach {
                    val msg = it.getValue(MessageModel::class.java)
                    if (msg != null) msgs.add(msg)
                }
                messages = msgs.sortedBy { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = receiverName,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = White
                        )
                        Text(
                            text = "Online",
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = Orange
                        )
                    }
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
        ) {


            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(messages) { message ->
                    val isMe = message.senderId == uid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMe) 16.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isMe) Orange else DarkCard)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = message.message,
                                    fontFamily = Poppins,
                                    fontSize = 14.sp,
                                    color = White
                                )
                                Text(
                                    text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        .format(Date(message.timestamp)),
                                    fontFamily = Poppins,
                                    fontSize = 9.sp,
                                    color = White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...", fontFamily = Poppins, color = GrayText) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (messageText.isNotEmpty()) {
                            val msgId = chatRef.push().key ?: ""
                            val message = MessageModel(
                                id = msgId,
                                senderId = uid,
                                receiverId = receiverId,
                                message = messageText,
                                timestamp = System.currentTimeMillis()
                            )
                            chatRef.child(msgId).setValue(message)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Orange)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(rememberNavController(), receiverId = "", receiverName = "John")
}