package com.sun.chat_04.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var idUser: String = "",
    var userName: String = "",
    var birthday: String? = null,
    var gender: String? = null,
    var bio: String = "",
    var pathAvatar: String = "",
    var pathBackground: String = "",
    var online: Int = 0,
    var lgn: Double = 0.0,
    var lat: Double = 0.0,
    var devicetoken: String = ""
) : Parcelable
