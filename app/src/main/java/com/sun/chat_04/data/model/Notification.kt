package com.sun.chat_04.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    val contents: String = "",
    val from: String = ""
) : Parcelable
