package com.sun.chat_04.ui.profile

import android.net.Uri
import com.sun.chat_04.data.model.User
import java.lang.Exception

interface ProfileContract {
    interface View {
        fun onGetUserProfileSuccess(user: User)

        fun onFailure(exception: Exception?)

        fun onUpdateUserAvatarSuccess(uri: Uri)

        fun onUpdateUserCoverSuccess(uri: Uri)
    }

    interface Presenter {
        fun getUserProfile()

        fun updateUserAvatar(uri: Uri)

        fun updateUserCover(uri: Uri)

        fun updateUserStatus(online: Int)
    }
}
