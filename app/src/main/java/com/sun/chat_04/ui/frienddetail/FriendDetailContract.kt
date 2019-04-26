package com.sun.chat_04.ui.frienddetail

interface FriendDetailContract {
    interface View {
        fun showButtonChat()

        fun showButtonInviteMoreFriends()

        fun showButtonCancelInviteMoreFriends()

        fun onFailure(exception: Exception?)

        fun onGetFriendImagesSuccess(images: List<String>?)

        fun showLoadingImages()

        fun hideLoadingImage()
    }

    interface Presenter {
        fun checkIsFriend(friendId: String)

        fun checkInvitedMoreFriends(userId: String, friendId: String)

        fun inviteMoreFriends(friendId: String)

        fun cancelInviteMoreFriends(friendId: String)

        fun getFriendImages()
    }
}
