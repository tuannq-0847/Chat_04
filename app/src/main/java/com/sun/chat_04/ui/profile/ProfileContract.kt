package com.sun.chat_04.ui.profile

import android.net.Uri
import com.sun.chat_04.data.model.User
import java.lang.Exception

interface ProfileContract {
    interface View {
        fun onGetUserProfileSuccess(user: User)

        fun onFailure(exception: Exception?)

        fun onUpdateUserImageSuccess(uri: Uri, field: String)
    }

    interface Presenter {
        fun getUserProfile()

        fun updateUserImage(uri: Uri, field: String)
    }
}
