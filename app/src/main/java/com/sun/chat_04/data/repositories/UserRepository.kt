package com.sun.chat_04.data.repositories

import android.location.Location
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
        remoteDataSource.handleFbLogin(token,callbackLogin)
    }

    override fun insertUser(user: User, email: String?, password: String?, callback: RemoteCallback<Boolean>) {
        remoteDataSource.insertUser(user, email, password, callback)
    }

    override fun upgradeLocationUser(location: Location, callback: RemoteCallback<Boolean>) {
        remoteDataSource.upgradeLocationUser(location, callback)
    }
}
