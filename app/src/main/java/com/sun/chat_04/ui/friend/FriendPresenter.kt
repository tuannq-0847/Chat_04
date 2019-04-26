package com.sun.chat_04.ui.friend

import android.util.Log
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Global

class FriendPresenter(
    private val view: FriendContract.View,
    private val repository: FriendRepository,
    private val userRepository: UserRepository
) : FriendContract.Presenter {

    override fun getFriends() {
        val userId = Global.firebaseAuth.currentUser?.uid
        repository.getFriends(userId, object : RemoteCallback<ArrayList<Friend>> {
            override fun onSuccessfuly(data: ArrayList<Friend>) {
                if (data.isEmpty()) {
                    view.showEmptyData()
                    return
                }
                updateStatusFriend(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onGetFriendsFailed(exception)
            }
        })
    }

    private fun updateStatusFriend(data: ArrayList<Friend>) {
        val friends: ArrayList<Friend> = ArrayList()
        for (friend in data) {
            userRepository.getUserInfo(friend.idUser, object : RemoteCallback<User> {
                override fun onSuccessfuly(data: User) {
                    val fr = Friend(
                        friend.idUser, data.pathAvatar, data.online,
                        friend.type, friend.contents, data.userName, friend.seen
                    )
                    friends.add(fr)
                    view.onGetFriendsSuccessfully(friends)
                }

                override fun onFailure(exception: Exception?) {
                    view.onGetFriendsFailed(exception)
                }
            })
        }
    }
}
