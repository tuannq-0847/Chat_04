package com.sun.chat_04.ui.frienddetail

import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Global

class FriendDetailPresenter(private val view: FriendDetailContract.View, private val repository: UserRepository) :
    FriendDetailContract.Presenter {

    override fun checkIsFriend(friendId: String) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.checkIsFriend(userId, friendId, object : RemoteCallback<Boolean> {
                override fun onSuccessfuly(data: Boolean) {
                    when {
                        data -> view.showButtonChat()
                        else -> checkInvitedMoreFriends(userId, friendId)
                    }
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun checkInvitedMoreFriends(userId: String, friendId: String) {
        repository.checkInvitedMoreFriends(userId, friendId, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                when {
                    data -> view.showButtonCancelInviteMoreFriends()
                    else -> view.showButtonInviteMoreFriends()
                }
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun cancelInviteMoreFriends(friendId: String) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.cancelInviteMoreFriends(userId, friendId, object : RemoteCallback<Boolean> {
                override fun onSuccessfuly(data: Boolean) {
                    view.showButtonInviteMoreFriends()
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun inviteMoreFriends(friendId: String) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (!userId.isEmpty()) {
            repository.inviteMoreFriend(userId, friendId, object : RemoteCallback<Boolean> {
                override fun onSuccessfuly(data: Boolean) {
                    view.showButtonCancelInviteMoreFriends()
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }

    override fun getFriendImages() {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (userId.isNotEmpty()) {
            view.showLoadingImages()
            repository.getUserImages(userId, object : RemoteCallback<List<String>> {
                override fun onSuccessfuly(data: List<String>) {
                    view.onGetFriendImagesSuccess(data)
                    view.hideLoadingImage()
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                    view.hideLoadingImage()
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }
}
