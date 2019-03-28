package com.sun.chat_04.ui.login

import com.facebook.AccessToken
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView

interface LoginContract {
    interface View : BaseView {
        fun onLoginListener(isSuccessful: Boolean)
        fun onSaveListener(isSuccessful: Boolean)
    }

    interface Presenter : BasePresenter {
        fun handleFbLogin(token: AccessToken?)
        fun loginByEmailAndPassword(email: String, pass: String)
    }
}
