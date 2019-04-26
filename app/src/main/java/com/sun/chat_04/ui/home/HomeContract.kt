package com.sun.chat_04.ui.home

import android.location.Location
import com.sun.chat_04.data.model.Notification
import java.lang.Exception

interface HomeContract {
    interface View {
        fun onUpgradeLocationSuccessful()

        fun onUpgradeLocationFailure()
        fun onFailure(exception: Exception?)
        fun onUpdateUserStatusSuccessful()
    }

    interface Presenter {
        fun upgradeLocationUser(location: Location)
        fun updateUserStatus(online: Int)
    }
}
