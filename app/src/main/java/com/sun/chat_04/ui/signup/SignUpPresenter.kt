package com.sun.chat_04.ui.signup

import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserRepository

class SignUpPresenter(val view: SignUpContract.View, val repository: UserRepository) : SignUpContract.Presenter {

    private val LENGTH_PASSWORD_MIN = 6
    private val GENDER_INVALID = "-1"

    override fun validateUser(user: User, email: String, password: String, passwordConfirmation: String): Boolean {
        if (user.userName.isNullOrEmpty()) {
            view.onEmptyUserName()
            return false
        }
        if (user.birthday.isNullOrEmpty()) {
            view.onEmptyBirthday()
            return false
        }

        if (user.gender.equals(GENDER_INVALID)) {
            view.onEmptyGender()
            return false
        }

        if (email.isEmpty()) {
            view.onEmptyEmail()
            return false
        }

        if (password.isEmpty()) {
            view.onEmptyPassword()
            return false
        }

        if (passwordConfirmation.isEmpty()) {
            view.onEmptyConfirmPassword()
            return false
        }

        if (password.length < LENGTH_PASSWORD_MIN) {
            view.onLengthPasswordInvalid()
            return false
        }

        if (!password.equals(passwordConfirmation)) {
            view.onCofirmPasswordInvalid()
            return false
        }
        return true
    }

    override fun signUp(user: User, email: String, password: String, passwordConfirmation: String) {
        if (!validateUser(user, email, password, passwordConfirmation)) {
            return
        }
        repository.insertUser(user, email, password, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
                view.onSignUpSuccessfuly()
            }

            override fun onFailure(exception: Exception?) {
            }
        })
    }
}
