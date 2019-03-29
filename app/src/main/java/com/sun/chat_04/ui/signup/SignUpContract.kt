package com.sun.chat_04.ui.signup

import com.sun.chat_04.data.model.User

interface SignUpContract {
    interface View {
        fun onSignUpSuccessfuly()
        fun onSignUpFailure()
        fun onEmptyUserName()
        fun onEmptyBirthday()
        fun onEmptyGender()
        fun onEmptyEmail()
        fun onEmptyPassword()
        fun onEmptyConfirmPassword()
        fun onLengthPasswordInvalid()
        fun onCofirmPasswordInvalid()
    }

    interface Presenter {
        fun validateUser(user: User, email: String, password: String, passwordConfirmation: String): Boolean

        fun signUp(user: User, email: String, password: String, passwordConfirmation: String)
    }
}
