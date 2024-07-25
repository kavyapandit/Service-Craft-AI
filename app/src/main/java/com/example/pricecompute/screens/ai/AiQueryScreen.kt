package com.example.pricecompute.screens.ai

import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pricecompute.provider.ComputeViewModel
import com.example.pricecompute.ui.theme.PriceComputeTheme
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ComputeViewModel = viewModel(factory = ComputeViewModel.Factory)
) {
    val chats by viewModel.chats.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            MessageInput(
                onSendMessage = viewModel::sendMessage,
                resetScroll = {
                    scope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        },
        containerColor = Color.Black
    ) {
        Column (
            modifier = Modifier
                .padding(it)
                .fillMaxSize()

        ){
            OutlinedButton(onClick = { viewModel.clearChat() }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Text(text = "Clear Chat", color = Color.White)
            }
            OutlinedButton(onClick = { viewModel.getNewMachine() }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Text(text = "Add machine", color = Color.White)
            }
            ChatList(messageList = chats, listState =listState)
        }
    }

}

@Composable
fun ChatList(
    messageList:List<ChatMsg>,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        reverseLayout = true
    ){
        items(messageList.reversed()){message->
            ChatItem(chatMsg = message)
        }
    }
}

@Composable
fun ChatItem(
    chatMsg: ChatMsg
) {
    val isModel = chatMsg.participant == 1 ||
            chatMsg.participant == 2

    val itemShape = if (isModel){
        RoundedCornerShape(4.dp,20.dp,20.dp,20.dp)
    }else{
        RoundedCornerShape(20.dp,4.dp,20.dp,20.dp)
    }

    val bgColor = when(chatMsg.participant){
        0->{
            Color(108, 157, 161)
        }
        1->{
            Color(35, 233, 247)
        }
        2->{
            MaterialTheme.colorScheme.errorContainer
        }

        else -> {
            MaterialTheme.colorScheme.inverseSurface
        }
    }

    val horizontalAlignment = if (isModel){
        Alignment.Start
    }else{
        Alignment.End
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = chatMsg.participant.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row {
            if (chatMsg.isPending){
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = itemShape,
                    modifier = Modifier.widthIn(0.dp,maxWidth*0.9f)
                ) {
                    Text(
                        text = chatMsg.text,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val ctx = LocalContext.current
    val kb = LocalSoftwareKeyboardController.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(21, 5, 43)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = userMessage,
                label = { Text(text = "prompt") },
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(108, 157, 161),
                    focusedLabelColor = Color(108, 157, 161)
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.85f)
            )
            IconButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                        kb?.hide()
                    }
                    else{
                        Toast.makeText(
                            ctx,
                            "Invalid Input",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.15f),
                colors = IconButtonDefaults.filledIconButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "send",
                    modifier = Modifier
                )
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun Items() {
    PriceComputeTheme {
        ChatScreen()
    }
}