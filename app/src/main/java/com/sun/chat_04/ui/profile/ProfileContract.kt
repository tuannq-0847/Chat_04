package com.sun.chat_04.ui.profile

import android.net.Uri
import com.sun.chat_04.data.model.User

interface ProfileContract {
    interface View {
        fun onGetUserProfileSuccess(user: User)

        fun onFailure(exception: Exception?)

        fun onUpdateUserAvatarSuccess(uri: Uri)

        fun onUpdateUserCoverSuccess(uri: Uri)

        fun onSignOutSuccessfully()

        fun onGetUserImagesSuccess(images: List<String>?)

        fun showLoadingImages()

        fun hideLoadingImage()
    }

    interface Presenter {
        fun getUserProfile()

        fun updateUserAvatar(uri: Uri)

        fun updateUserCover(uri: Uri)

        fun updateUserStatus(online: Int)

        fun getUserImages()
    }
}
