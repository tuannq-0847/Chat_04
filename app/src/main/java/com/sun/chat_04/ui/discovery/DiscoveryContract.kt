package com.sun.chat_04.ui.discovery

import android.location.Location
import android.support.annotation.NonNull
import com.sun.chat_04.data.model.User

interface DiscoveryContract {
    interface View {
        fun onFindUsersSuccess(users: List<User>)

        fun onFindUserFailure(exception: Exception?)

        fun onGetUserInfoSuccess(user: User)
    }

    interface Presenter {
        fun findUserByName(@NonNull userName: String)

        fun getUserInfo()

        fun findUserAroundHere(@NonNull user: User)

        fun isNearbyUser(location: Location?, @NonNull friend: User): Boolean
    }
}
