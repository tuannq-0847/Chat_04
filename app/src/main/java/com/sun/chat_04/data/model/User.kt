package com.sun.chat_04.data.model

data class User(
    val idUser: String = "",
    val userName: String? = null,
    val birthday: String? = null,
    val gender: String? = null,
    val bio: String? = null,
    val pathAvatar: String? = null,
    val pathBackground: String? = null,
    var isOnline: Int = 0,
    var lgn: Double = 0.0,
    var lat: Double = 0.0
)
