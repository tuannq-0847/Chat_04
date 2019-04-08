package com.sun.chat_04.data.model

import java.io.Serializable
data class Friend(
    val idUser: String = "",
    var avatarLink: String? = null,
    var isonline: Int = 0,
    var contents: String = "",
    var userName: String = ""
) : Serializable
