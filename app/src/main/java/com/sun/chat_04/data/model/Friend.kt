package com.sun.chat_04.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friend(
    val idUser: String = "",
    var avatarLink: String? = null,
    var isonline: Int = 0,
    var type: String = "",
    var contents: String = "",
    var userName: String = "",
    var seen: Int = 0
) : Parcelable
