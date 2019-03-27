package com.sun.chat_04.data.model

data class User(
    var idUser: String = "",
    var userName: String? = null,
    var birthday: String? = null,
    var gender: String? = null,
    var bio: String = "",
    var pathAvatar: String = "",
    var pathBackground: String = "",
    var isOnline: Int = 0,
    var lgn: Double = 0.0,
    var lat: Double = 0.0
)
