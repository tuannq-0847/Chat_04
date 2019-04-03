package com.sun.chat_04.ui.userdetail

import com.sun.chat_04.data.repositories.UserRepository

class UserDetailPresenter(private val view: UserDetailContract.View, private val repository: UserRepository) :
    UserDetailContract.Presenter {

    override fun checkFriend(friendId: String) {
        // Check friend
    }

    override fun checkInvitedAddFriend(friendId: String) {
        // Check invite add friend
    }

    override fun cancelInviteAddFriend(friendId: String) {
        // Cancel add friend
    }

    override fun inviteAddFriend(friendId: String) {
        // invite add friend
    }
}
