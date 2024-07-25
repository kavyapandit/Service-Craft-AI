package com.example.pricecompute.screens.ai

import androidx.compose.runtime.toMutableStateList


class ChatUiState(
    messages: List<ChatMsg> = emptyList()
) {
    private val _messages: MutableList<ChatMsg> = messages.toMutableStateList()
    val messages: List<ChatMsg> = _messages

    fun addMessage(msg: ChatMsg) {
        _messages.add(msg)
    }

    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}