package com.sun.chat_04.ui.profile

import android.net.Uri
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Global

class ProfilePresenter(val view: ProfileContract.View, val repository: UserRepository) : ProfileContract.Presenter {

    override fun getUserProfile() {
        val userId = Global.firebaseAuth.currentUser?.uid
        if (userId != null) {
            repository.getUserInfo(userId, object : RemoteCallback<User> {
                override fun onSuccessfuly(data: User) {
                    view.onGetUserProfileSuccess(data)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun updateUserImage(uri: Uri, field: String) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.insertUserImage(userId, uri, field, object : RemoteCallback<Uri> {
                override fun onSuccessfuly(data: Uri) {
                    view.onUpdateUserImageSuccess(uri, field)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }
}
