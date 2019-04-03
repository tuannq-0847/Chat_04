package com.sun.chat_04.ui.discovery

import android.location.Location
import com.sun.chat_04.data.model.User

interface DiscoveryContract {
    interface View {
        fun onFindUsersSuccess(users: List<User>)

        fun onFindUserFailure()

        fun onGetInfoUserSuccess(user: User)
    }

    interface Presenter {
        fun findUserByName(userName: String)

        fun getUserInfo()

        fun findUserAroundHere(location: Location?)

        fun checkDistanceBetweenUsers(location: Location?, user: User) : Boolean
    }
}
