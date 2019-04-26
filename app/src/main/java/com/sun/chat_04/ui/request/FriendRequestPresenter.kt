package com.sun.chat_04.ui.request

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.data.repositories.FriendRequestRepository
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import java.util.Collections

class FriendRequestPresenter(
    private val view: FriendRequestContract.View,
    private val repository: FriendRequestRepository,
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) : FriendRequestContract.Presenter {

    override fun getUserFromFriend(friend: Friend) {
        userRepository.getUserInfo(friend.idUser, object : RemoteCallback<User> {
            override fun onSuccessfuly(data: User) {
                view.onGetUserSuccessfully(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun sortByName(friends: ArrayList<Friend>): ArrayList<Friend> {
        friends.sortWith(Comparator { o1, o2 ->
            return@Comparator o1.userName.compareTo(o2.userName)
        })
        return friends
    }

    override fun deleteFriend(friend: Friend) {
        friendRepository.unFriend(friend, object : RemoteCallback<String> {
            override fun onSuccessfuly(data: String) {
                view.onDeleteFriendSuccessfully(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun getFriends() {
        val uid = Global.firebaseAuth.currentUser?.uid
        uid?.let {
            friendRepository.getFriends(uid, object : RemoteCallback<ArrayList<Friend>> {
                override fun onSuccessfuly(data: ArrayList<Friend>) {
                    updateStatusFriend(data)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
        }
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
                    view.onGetFriendsSuccesfully(friends)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
        }
        if (friends.isEmpty()) {
            view.showEmptyFriends()
        }
    }

    override fun approveFriendRequest(user: User) {
        repository.approveFriendRequest(user, object : RemoteCallback<String> {
            override fun onSuccessfuly(data: String) {
                if (data != Constants.NONE) {
                    view.onApproveSuccessfully(data)
                }
            }

            override fun onFailure(exception: Exception?) {
                view.onFailure(exception)
            }
        })
    }

    override fun cancelFriendRequest(user: User) {
        repository.cancelFriendRequest(user, object : RemoteCallback<String> {
            override fun onSuccessfuly(data: String) {
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
