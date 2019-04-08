package com.sun.chat_04.data.model

data class Message(
    val id: String = "",
    var contents: String = "",
    var from: String = "",
    var type: String = ""
)
