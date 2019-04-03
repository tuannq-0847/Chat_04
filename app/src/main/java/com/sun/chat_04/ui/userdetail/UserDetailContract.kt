package com.sun.chat_04.ui.userdetail

interface UserDetailContract {
    interface View {
        fun isFriend(result: Boolean)

        fun isInvitedAddFriend(result: Boolean)

        fun onFailure(exception: Exception?)

        fun onCancelInviteAddFriendSuccess()
    }

    interface Presenter {
        fun checkFriend(friendId: String)

        fun checkInvitedAddFriend(friendId: String)

        fun inviteAddFriend(friendId: String)

        fun cancelInviteAddFriend(friendId: String)
    }
}
