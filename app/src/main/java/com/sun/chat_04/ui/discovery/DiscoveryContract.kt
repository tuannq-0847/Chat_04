package com.sun.chat_04.ui.discovery

import android.support.annotation.NonNull
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.model.UserDistanceWrapper

interface DiscoveryContract {
    interface View {
        fun onFindUsersSuccess(userDistanceWrappers: List<UserDistanceWrapper>)

        fun onFindUserFailure(exception: Exception?)

        fun onGetUserInfoSuccess(user: User)

        fun showProgress()

        fun hideProgress()

        fun hideSwipeRefreshDiscovery()

        fun showTitleSuggestFriends()

        fun showTitleFindFriendsByName()

        fun showImageEmpty()

        fun hideImageEmpty()
    }

    interface Presenter {
        fun findUserByName(@NonNull userName: String)

        fun getUserInfo()

        fun findUserAroundHere(@NonNull user: User, @NonNull distance: Int)

        fun distanceBetweenUser(@NonNull user: User, @NonNull friend: User): Float
    }
}
