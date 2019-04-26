package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.remote.FriendRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class FriendRepository(private val remote: FriendRemoteDataSource) : FriendDataSource.Remote {
    override fun unFriend(friend: Friend, callback: RemoteCallback<String>) {
        remote.unFriend(friend, callback)
    }

    override fun getFriends(userId: String?, callback: RemoteCallback<ArrayList<Friend>>) {
        remote.getFriends(userId, callback)
    }
}
