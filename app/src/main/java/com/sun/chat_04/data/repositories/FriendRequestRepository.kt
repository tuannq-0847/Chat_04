package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.FriendRequestRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class FriendRequestRepository(private val remote: FriendRequestRemoteDataSource) : FriendRequestDataSource.Remote {
    override fun approveFriendRequest(user: User, callback: RemoteCallback<Boolean>) {
        remote.approveFriendRequest(user, callback)
    }

    override fun cancelFriendRequest(user: User, callback: RemoteCallback<Boolean>) {
        remote.cancelFriendRequest(user, callback)
    }

    override fun showFriendRequests(callback: RemoteCallback<ArrayList<User>>) {
        remote.showFriendRequests(callback)
    }
}
