package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.signup.RemoteCallback

interface FriendDataSource {
    interface Remote {
        fun getFriends(userId: String?, callback: RemoteCallback<ArrayList<Friend>>)
        fun unFriend(friend: Friend, callback: RemoteCallback<String>)
    }
}
