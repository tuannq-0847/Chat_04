package com.sun.chat_04.ui.request

import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.FriendRequestRepository
import com.sun.chat_04.ui.signup.RemoteCallback

class FriendRequestPresenter(
    private val view: FriendRequestContract.View,
    private val repository: FriendRequestRepository
) : FriendRequestContract.Presenter {

    override fun approveFriendRequest(user: User) {
        repository.approveFriendRequest(user, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onApproveSuccessfully(user.userName)
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun cancelFriendRequest(user: User) {
        repository.cancelFriendRequest(user, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onCancelSuccessfully()
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun getFriendRequests() {
        repository.showFriendRequests(object : RemoteCallback<ArrayList<User>> {
            override fun onSuccessfuly(data: ArrayList<User>) {
                view.onFriendRequestsAvailable(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }
}
