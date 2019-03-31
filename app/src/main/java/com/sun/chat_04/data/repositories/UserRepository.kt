package com.sun.chat_04.data.repositories

import com.facebook.AccessToken
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.login.RemoteCallBackLogin
import com.sun.chat_04.ui.signup.RemoteCallback

class UserRepository(private val remoteDataSource: UserDataSource.Remote) : UserDataSource.Remote {
    override fun handleFbLogin(
        token: AccessToken?,
        callback: RemoteCallback<Boolean>,
        callbackLogin: RemoteCallBackLogin
    ) {
        remoteDataSource.handleFbLogin(token, callback, callbackLogin)
    }

    override fun insertUser(user: User, email: String?, password: String?, callback: RemoteCallback<Boolean>) {
        remoteDataSource.insertUser(user, email, password, callback)
    }
}
