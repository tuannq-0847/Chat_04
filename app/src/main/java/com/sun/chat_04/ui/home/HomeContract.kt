package com.sun.chat_04.ui.home

import android.location.Location

interface HomeContract {
    interface View {
        fun onUpgradeLocationSuccessful()

        fun onUpgradeLocationFailure()
    }

    interface Presenter {
        fun upgradeLocationUser(location: Location)
    }
}
