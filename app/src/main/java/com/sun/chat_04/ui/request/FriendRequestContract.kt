package com.sun.chat_04.ui.request

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import java.lang.Exception

interface FriendRequestContract {
    interface View {
        fun onFriendRequestsAvailable(friendRequests: ArrayList<User>)
        fun onFailure(exception: Exception?)
        fun onApproveSuccessfully(userName: String?)
        fun onCancelSuccessfully()
        fun onGetFriendsSuccesfully(friends: ArrayList<Friend>)
        fun onDeleteFriendSuccessfully(userName: String)
        fun onGetUserSuccessfully(user: User)
        fun showEmptyFriends()
    }

    interface Presenter {
        fun getFriends()
        fun getFriendRequests()
        fun approveFriendRequest(user: User)
        fun cancelFriendRequest(user: User)
        fun deleteFriend(friend: Friend)
        fun sortByName(friends: ArrayList<Friend>): ArrayList<Friend>
        fun getUserFromFriend(friend: Friend)
    }
}
