package com.sun.chat_04.ui.discovery

import android.location.Location
import android.support.annotation.NonNull
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.model.User
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
                view.onFindUsersSuccess(users)
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

    override fun findUserAroundHere(@NonNull user: User) {
        val locationUser = Location("")
        locationUser.latitude = user.lat
        locationUser.longitude = user.lgn
        repository.getUsers(object : RemoteCallback<List<User>> {
            override fun onSuccessfuly(data: List<User>) {
                val users = ArrayList<User>()
                for (friend in data) {
                    if (isNearByUser(locationUser, friend)) {
                        users.add(friend)
                    }
                }
                view.onFindUsersSuccess(users)
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

    override fun isNearByUser(location: Location?, @NonNull friend: User): Boolean {
        val locationB = Location("")
        locationB.latitude = friend.lat
        locationB.longitude = friend.lgn
        val distance = location?.distanceTo(locationB)?.div(Constants.CONVERT_KM) ?: return false
        if (distance <= Constants.MAX_DISTANCE) return true
        return false
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
