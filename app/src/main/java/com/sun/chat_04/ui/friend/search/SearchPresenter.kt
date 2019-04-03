package com.sun.chat_04.ui.friend.search

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global

class SearchPresenter(
    private val view: SearchConstract.View,
    private val repository: FriendRepository
) : SearchConstract.Presenter {

    override fun searchUser(query: String) {
        var lastMessages: ArrayList<Friend> = ArrayList()
        val currentUserId = Global.firebaseAuth.currentUser?.uid
        repository.getFriends(currentUserId, object : RemoteCallback<ArrayList<Friend>> {
            override fun onSuccessfuly(data: ArrayList<Friend>) {
                lastMessages.clear()
                lastMessages = data.filter {
                    it.userName.toLowerCase().contains(query.toLowerCase())
                } as ArrayList<Friend>
                if (query == Constants.NONE) {
                    lastMessages.clear()
                }
                view.onGetUsersSuccessfully(lastMessages)
            }

            override fun onFailure(exception: Exception?) {
                view.onGetUsersFailure(exception)
            }
        })
    }
}
