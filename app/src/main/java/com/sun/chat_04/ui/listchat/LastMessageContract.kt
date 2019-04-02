package com.sun.chat_04.ui.listchat

import com.google.firebase.database.DatabaseError
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import java.lang.Exception

interface LastMessageContract {
    interface View : BaseView {
        fun onGetLastMessagesSuccessfully(lastMessages: ArrayList<LastMessage>)
        fun onGetLastMessagesFailed(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun getLastMessages()
    }
}
