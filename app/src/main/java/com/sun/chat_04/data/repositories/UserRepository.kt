package com.sun.chat_04.data.repositories

import android.location.Location
import android.net.Uri
import com.facebook.AccessToken
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

class UserRepository(private val remoteDataSource: UserDataSource.Remote) : UserDataSource.Remote {

    override fun loginByEmailAndPassword(email: String, password: String, callback: RemoteCallback<Boolean>) {
        remoteDataSource.loginByEmailAndPassword(email, password, callback)
    }

    override fun handleFbLogin(
        token: AccessToken?,
        callbackLogin: RemoteCallback<Boolean>
    ) {
        remoteDataSource.handleFbLogin(token, callbackLogin)
    }

    override fun insertUser(user: User, email: String?, password: String?, callback: RemoteCallback<Boolean>) {
        remoteDataSource.insertUser(user, email, password, callback)
    }

    override fun upgradeLocationUser(location: Location, callback: RemoteCallback<Boolean>) {
        remoteDataSource.upgradeLocationUser(location, callback)
    }

    override fun getUsers(callback: RemoteCallback<List<User>>) {
        remoteDataSource.getUsers(callback)
    }

    override fun getUserInfo(userId: String, callback: RemoteCallback<User>) {
        remoteDataSource.getUserInfo(userId, callback)
    }

    override fun insertUserImage(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>) {
        remoteDataSource.insertUserImage(userId, uri, field, callback)
    }

    override fun insertUserImagePath(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>) {
        remoteDataSource.insertUserImagePath(userId, uri, field, callback)
    }

    override fun editUserProfile(user: User, callback: RemoteCallback<Boolean>) {
        remoteDataSource.editUserProfile(user, callback)
    }
}
