package com.sun.chat_04.ui.chat

import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView

interface ChatContract {
    interface View : BaseView {
        fun onGetMessagesFailure(task: Exception?)
        fun onGetMessagesSuccessfully(messages: ArrayList<Message>)
    }

    interface Presenter : BasePresenter {
        fun getMessages()
        fun handleSendMessage(message: Message)
    }
}
