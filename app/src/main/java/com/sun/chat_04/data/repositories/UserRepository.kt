package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

class UserRepository(val remoteDataSource: UserDataSource.Remote) : UserDataSource.Remote {

    override fun insertUser(user: User, email: String, password: String, callback: RemoteCallback<Boolean>) {
        remoteDataSource.insertUser(user, email, password, callback)
    }
}
