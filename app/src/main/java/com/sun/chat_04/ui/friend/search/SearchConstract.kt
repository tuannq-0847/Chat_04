package com.sun.chat_04.ui.friend.search

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView

interface SearchConstract {
    interface View : BaseView {
        fun onGetUsersSuccessfully(friends: ArrayList<Friend>)
        fun onGetUsersFailure(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun searchUser(query: String)
    }
}
