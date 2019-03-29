package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

interface UserDataSource {
    interface Remote {
        fun insertUser(user: User, email: String, password: String, callback: RemoteCallback<Boolean>)
    }
}
