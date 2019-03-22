package com.sun.chat_04.ui.login

import android.app.usage.ConfigurationStats
import android.util.Log
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.ui.signup.SignUpContract
import com.sun.chat_04.util.Constants
import java.lang.Exception

class LoginPresenter(
    private val repository: UserRepository,
    private val view: LoginContract.View
) : LoginContract.Presenter {

    override fun loginByEmailAndPassword(email: String, password: String) {
        repository.loginByEmailAndPassword(email, password, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onLoginSuccessfully()
            }

            override fun onFailure(exception: Exception?) {
                checkErrorLogin(exception)
            }
        })
    }

    override fun checkValidateLogin(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            view.validLogin()
        } else {
            view.invalidLogin()
        }
    }

    override fun onSuccess(result: LoginResult?) {
        repository.handleFbLogin(result?.accessToken, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
            }

            override fun onFailure(exception: Exception?) {
            }
        })
    }

    override fun onCancel() {
    }

    override fun onError(error: FacebookException?) {
        view.onGetTokenFail(error)
    }

    private fun checkErrorLogin(exception: Exception?) {
        val firebaseAuthException = exception as FirebaseAuthException
        when (firebaseAuthException.errorCode) {
            Constants.INVALID_EMAIL -> {
                view.onLoginFailure(R.string.bad_email)
            }
            Constants.ERROR_USER_NOT_FOUND -> {
                view.onLoginFailure(R.string.user_not_found)
            }
            else -> {
                view.onLoginFailure(R.string.other_error)
            }
        }
    }
}
