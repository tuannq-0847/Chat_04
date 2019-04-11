package com.sun.chat_04.ui.chat

import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import java.io.InputStream

interface ChatContract {
    interface View : BaseView {
        fun onGetMessagesFailure(task: Exception?)
        fun onGetMessagesSuccessfully(messages: ArrayList<Message>)
        fun insertMessageSuccessfully()
        fun insertMessageFailure(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun compressBitmap(inputStream: InputStream?): ByteArray
        fun getMessages()
        fun handleMessage(message: Message)
    }
}
