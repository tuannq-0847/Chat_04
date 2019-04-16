package com.sun.chat_04.ui.profile

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global

class ProfilePresenter(val view: ProfileContract.View, val repository: UserRepository) : ProfileContract.Presenter {
    override fun getUserProfile() {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
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

    override fun updateUserAvatar(uri: Uri) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.insertUserImage(userId, uri, Constants.PATH_AVATAR, object : RemoteCallback<Uri> {
                override fun onSuccessfuly(data: Uri) {
                    view.onUpdateUserAvatarSuccess(uri)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun updateUserCover(uri: Uri) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.insertUserImage(userId, uri, Constants.PATH_COVER, object : RemoteCallback<Uri> {
                override fun onSuccessfuly(data: Uri) {
                    view.onUpdateUserCoverSuccess(uri)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun updateUserStatus(online: Int) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        Log.d("TAG", userId)
        if (userId.isNotEmpty()) {
            repository.updateUserStatus(userId, online, object : RemoteCallback<Boolean> {
                override fun onSuccessfuly(data: Boolean) {
                    view.onSignOutSuccessfully()
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
