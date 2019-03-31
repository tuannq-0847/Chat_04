package com.sun.chat_04.ui.login

import java.lang.Exception

interface RemoteCallBackLogin {
    fun onLoginSuccessfully()
    fun onLoginFailure(exception: Exception?)
}