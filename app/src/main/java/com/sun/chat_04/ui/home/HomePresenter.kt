package com.sun.chat_04.ui.home

import android.location.Location
import com.google.firebase.database.DatabaseException
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Global

class HomePresenter(
    private val view: HomeContract.View,
    private val userRepository: UserRepository
) : HomeContract.Presenter {

    override fun upgradeLocationUser(location: Location) {
        userRepository.upgradeLocationUser(location, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onUpgradeLocationSuccessful()
            }

            override fun onFailure(exception: Exception?) {
                view.onUpgradeLocationFailure()
            }
        })
    }

    override fun updateUserStatus(online: Int) {
        val userId = Global.firebaseAuth.currentUser?.uid.toString()
        if (userId.isNotEmpty()) {
            userRepository.updateUserStatus(userId, online, object : RemoteCallback<Boolean> {
                override fun onSuccessfuly(data: Boolean) {
                    view.onUpdateUserStatusSuccessful()
                }

                override fun onFailure(exception: Exception?) {
                    view.onFailure(exception)
                }
            })
            return
        }
        view.onFailure(DatabaseException(""))
    }
}
