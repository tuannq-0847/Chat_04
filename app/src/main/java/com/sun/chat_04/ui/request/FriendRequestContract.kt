package com.sun.chat_04.ui.request

import com.sun.chat_04.data.model.User
import java.lang.Exception

interface FriendRequestContract {
    interface View {
        fun onFriendRequestsAvailable(friendRequests: ArrayList<User>)
        fun onFailure(exception: Exception?)
        fun onApproveSuccessfully(userName: String?)
        fun onCancelSuccessfully()
    }

    interface Presenter {
        fun getFriendRequests()
        fun approveFriendRequest(user: User)
        fun cancelFriendRequest(user: User)
    }
}