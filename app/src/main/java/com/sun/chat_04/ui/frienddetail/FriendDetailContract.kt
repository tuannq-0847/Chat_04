package com.sun.chat_04.ui.frienddetail

interface FriendDetailContract {
    interface View {
        fun showButtonChat()

        fun showButtonInviteMoreFriends()

        fun showButtonCancelInviteMoreFriends()

        fun onFailure(exception: Exception?)
    }

    interface Presenter {
        fun checkIsFriend(friendId: String)

        fun checkInvitedMoreFriends(userId: String, friendId: String)

        fun inviteMoreFriends(friendId: String)

        fun cancelInviteMoreFriends(friendId: String)
    }
}
