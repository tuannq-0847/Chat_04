package com.sun.chat_04.ui.login

import com.facebook.AccessToken
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import java.lang.Exception

interface LoginContract {
    interface View : BaseView {
        fun onLoginSuccessfully()
        fun onLoginFailure(message: Int)
        fun validLogin(isButtonClicked: Boolean)
        fun invalidLogin()
        fun onSaveSuccesfully()
        fun onSaveFailure(exception: Exception?)
        fun onGetTokenFail(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun loginByEmailAndPassword(email: String, password: String)
        fun checkValidateLogin(email: String, password: String, isButtonClicked: Boolean)
    }
}
