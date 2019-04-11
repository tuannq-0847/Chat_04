package com.sun.chat_04.ui.profile

import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class EditProfilePresenter(private val view: EditProfileContract.View, private val repository: UserRepository) :
    EditProfileContract.Presenter {

    override fun validateNewUserProfile(user: User): Boolean {
        if (user.userName.isNullOrEmpty()) {
            view.onEmptyNewName()
            return false
        }

        if (user.gender.equals(Constants.GENDER_INVALID)) {
            view.onEmptyNewGender()
            return false
        }

        if (user.birthday.isNullOrEmpty()) {
            view.onEmptyNewBirthday()
            return false
        }
        return true
    }

    override fun editUserProfile(user: User) {
        if (!validateNewUserProfile(user)) {
            return
        }
        repository.editUserProfile(user, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onEditUserProfileSuccess()
            }

            override fun onFailure(exception: Exception?) {
                view.onEditUserProfileFailure(exception)
            }
        })
    }
}
