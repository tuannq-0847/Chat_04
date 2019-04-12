package com.sun.chat_04.ui.frienddetail

import com.sun.chat_04.data.repositories.UserRepository

class FriendDetailPresenter(private val view: FriendDetailContract.View, private val repository: UserRepository) :
    FriendDetailContract.Presenter {

    override fun checkIsFriend(friendId: String) {
        // Check is friend
    }

    override fun checkInvitedMoreFriends(friendId: String) {
        // Check invite more friends
    }

    override fun cancelInviteMoreFriends(friendId: String) {
        // Cancel invite more friend
    }

    override fun inviteMoreFriends(friendId: String) {
        // invite add friend
    }
}
