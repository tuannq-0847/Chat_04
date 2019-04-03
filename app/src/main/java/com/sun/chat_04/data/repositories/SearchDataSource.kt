package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

interface SearchDataSource {
    interface Remote {
        fun getListUserQuery(callBack: RemoteCallback<ArrayList<User>>)
    }
}