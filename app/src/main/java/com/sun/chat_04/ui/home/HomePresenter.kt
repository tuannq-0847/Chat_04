package com.sun.chat_04.ui.home

import android.location.Location
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback

class HomePresenter(val view: HomeContract.View, val userRepository: UserRepository) : HomeContract.Presenter {

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
}
