package com.sun.chat_04.ui.signup

import java.lang.Exception

interface RemoteCallback<T> {
    fun onSuccessfuly(data: T)

    fun onFailure(exception: Exception)
}
