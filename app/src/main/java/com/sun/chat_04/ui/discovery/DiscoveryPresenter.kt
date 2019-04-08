package com.sun.chat_04.ui.discovery

import android.location.Location
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global

class DiscoveryPresenter(val view: DiscoveryContract.View, val userRepository: UserRepository) :
    DiscoveryContract.Presenter {

    override fun findUserByName(userName: String) {
        userRepository.getUsers(object : RemoteCallback<List<User>> {
            override fun onSuccessfuly(data: List<User>) {
                val users = ArrayList<User>()
                for (user in data) {
                    if (user.userName.equals(userName, ignoreCase = true))
                        users.add(user)
                }
                view.onFindUsersSuccess(users)
            }

            override fun onFailure(exception: Exception?) {
                view.onFindUserFailure(exception)
            }
        })
    }

    override fun findUserAroundHere(location: Location?) {
        userRepository.getUsers(object : RemoteCallback<List<User>> {
            override fun onSuccessfuly(data: List<User>) {
                val users = ArrayList<User>()
                for (user in data) {
                    if (isNearbyUser(location, user))
                        users.add(user)
                }
                view.onFindUsersSuccess(users)
            }

            override fun onFailure(exception: Exception?) {
                view.onFindUserFailure(exception)
            }
        })
    }

    override fun isNearbyUser(location: Location?, user: User): Boolean {
        val locationB = Location("")
        locationB.latitude = user.lat
        locationB.longitude = user.lgn
        val distance = location?.distanceTo(locationB)?.div(Constants.CONVERT_KM)
        if (distance == null) return false
        if (distance <= Constants.MAX_DISTANCE) return true
        return false
    }

    override fun getUserInfo() {
        val userId = Global.firebaseAuth.currentUser?.uid
        if (userId != null) {
            userRepository.getUserInfo(userId, object : RemoteCallback<User> {
                override fun onSuccessfuly(data: User) {
                    view.onGetInfoUserSuccess(data)
                }

                override fun onFailure(exception: Exception?) {
                    view.onFindUserFailure(exception)
                }
            })
            return
        }
        view.onFindUserFailure(DatabaseException(""))
    }
}
