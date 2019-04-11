package com.sun.chat_04.data.repositories

import android.location.Location
import android.net.Uri
import com.facebook.AccessToken
import com.google.firebase.storage.FirebaseStorage
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

        fun insertUserImage(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>)

        fun insertUserImagePath(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>)

        fun editUserProfile(user: User, callback: RemoteCallback<Boolean>)
    }
}
