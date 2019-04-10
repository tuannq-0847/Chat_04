package com.sun.chat_04.ui.chat

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView

interface ChatContract {
    interface View : BaseView {
        fun onGetMessagesFailure(task: Exception?)
        fun onGetMessagesSuccessfully(messages: ArrayList<Message>)
        fun insertMessageSuccessfully()
        fun insertMessageFailure(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun getMessages()
        fun handleSendMessage(message: Message, bitmap: Bitmap?)
    }
}
