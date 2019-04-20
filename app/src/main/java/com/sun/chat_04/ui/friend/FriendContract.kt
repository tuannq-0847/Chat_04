package com.sun.chat_04.ui.friend

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import java.lang.Exception

interface FriendContract {
    interface View : BaseView {
        fun onGetFriendsSuccessfully(friends: ArrayList<Friend>)
        fun onGetFriendsFailed(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun getFriends()
    }
}
