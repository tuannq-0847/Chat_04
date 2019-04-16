package com.sun.chat_04.ui.discovery

import android.location.Location
import android.support.annotation.NonNull
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.model.UserDistanceWrapper
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global

class DiscoveryPresenter(val view: DiscoveryContract.View, val repository: UserRepository) :
    DiscoveryContract.Presenter {

    override fun findUserByName(@NonNull userName: String) {
        view.showProgress()
        view.showTitleFindFriendsByName()
        repository.getUsers(object : RemoteCallback<List<User>> {
            override fun onSuccessfuly(data: List<User>) {
                val users = data.filter {
                    it.userName.toLowerCase().contains(userName.toLowerCase())
                }
                val userDistanceWrappers = users.map {
                    UserDistanceWrapper(it, Constants.DISTANCE_NOT_EXIST)
                }
                view.onFindUsersSuccess(userDistanceWrappers)
                when {
                    userDistanceWrappers.isNullOrEmpty() -> view.showImageEmpty()
                    else -> view.hideImageEmpty()
                }
                view.hideSwipeRefreshDiscovery()
                view.hideProgress()
            }

            override fun onFailure(exception: Exception?) {
                view.onFindUserFailure(exception)
                view.hideProgress()
                view.hideSwipeRefreshDiscovery()
            }
        })
    }

    override fun findUserAroundHere(@NonNull user: User, @NonNull distance: Int) {
        view.showProgress()
        view.showTitleSuggestFriends()
        repository.getUsers(object : RemoteCallback<List<User>> {
            override fun onSuccessfuly(data: List<User>) {
                val userDistanceWrappers = ArrayList<UserDistanceWrapper>()
                for (friend in data) {
                    val dis = distanceBetweenUser(user, friend)
                    if (dis <= distance.toFloat()) {
                        userDistanceWrappers.add(UserDistanceWrapper(friend, dis))
                    }
                }
                userDistanceWrappers.sort()
                view.onFindUsersSuccess(userDistanceWrappers)
                when {
                    userDistanceWrappers.isNullOrEmpty() -> view.showImageEmpty()
                    else -> view.hideImageEmpty()
                }
                view.hideSwipeRefreshDiscovery()
                view.hideProgress()
            }

            override fun onFailure(exception: Exception?) {
                view.onFindUserFailure(exception)
                view.hideProgress()
                view.hideSwipeRefreshDiscovery()
            }
        })
    }

    override fun distanceBetweenUser(@NonNull user: User, @NonNull friend: User): Float {
        val locationUser = Location("")
        locationUser.latitude = user.lat
        locationUser.longitude = user.lgn

        val locationFriend = Location("")
        locationFriend.latitude = friend.lat
        locationFriend.longitude = friend.lgn

        val distance = locationUser.distanceTo(locationFriend).div(Constants.CONVERT_KM)
        return (Math.round(distance * Constants.ROUNDING) / Constants.ROUNDING).toFloat()
    }

    override fun getUserInfo() {
        view.showProgress()
        val userId = Global.firebaseAuth.currentUser?.uid
        if (userId != null) {
            repository.getUserInfo(userId, object : RemoteCallback<User> {
                override fun onSuccessfuly(data: User) {
                    view.onGetUserInfoSuccess(data)
                    view.showTitleSuggestFriends()
                }

                override fun onFailure(exception: Exception?) {
                    view.onFindUserFailure(exception)
                    view.hideProgress()
                    view.hideSwipeRefreshDiscovery()
                }
            })
            return
        }
        view.onFindUserFailure(DatabaseException(""))
        view.hideProgress()
        view.hideSwipeRefreshDiscovery()
    }
}
