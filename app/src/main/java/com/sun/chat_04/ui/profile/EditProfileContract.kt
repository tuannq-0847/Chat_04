package com.sun.chat_04.ui.profile

import com.sun.chat_04.data.model.User

interface EditProfileContract {
    interface View {
        fun onEmptyNewName()

        fun onEmptyNewGender()

        fun onEmptyNewBirthday()

        fun onEditUserProfileSuccess()

        fun onEditUserProfileFailure(exception: Exception?)
    }

    interface Presenter {
        fun validateNewUserProfile(user: User): Boolean

        fun editUserProfile(user: User)
    }
}
