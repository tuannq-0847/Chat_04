package com.sun.chat_04.data.repositories

import android.location.Location
import com.facebook.AccessToken
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

interface UserDataSource {
    interface Remote {
        fun insertUser(user: User, email: String?, password: String?, callback: RemoteCallback<Boolean>)

        fun handleFbLogin(
            token: AccessToken?,
            callbackLogin: RemoteCallback<Boolean>
        )

        fun loginByEmailAndPassword(email: String, password: String, callback: RemoteCallback<Boolean>)

        fun upgradeLocationUser(location: Location, callback: RemoteCallback<Boolean>)

        fun getUsers(callback: RemoteCallback<List<User>>)

        fun getUserInfo(userId: String, callback: RemoteCallback<User>)
    }
}
