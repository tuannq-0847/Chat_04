package com.sun.chat_04.ui.login

import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView

interface LoginContract {
    interface View : BaseView {
        fun onLoginSuccessfully()
        fun onLoginFailure(message: Int)
        fun validLogin()
        fun invalidLogin()
        fun onSaveSuccesfully()
        fun onSaveFailure(exception: Exception?)
        fun onGetTokenFail(exception: Exception?)
    }

    interface Presenter : BasePresenter, FacebookCallback<LoginResult> {
        fun loginByEmailAndPassword(email: String?, password: String?)
        fun checkValidateLogin(email: String, password: String)
        fun isLogin(currentUser: FirebaseUser?): Boolean
    }
}
