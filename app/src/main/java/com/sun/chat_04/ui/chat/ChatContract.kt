package com.sun.chat_04.ui.chat

import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import com.sun.chat_04.ui.signup.RemoteCallback
import java.io.InputStream

interface ChatContract {
    interface View : BaseView {
        fun onGetMessagesFailure(task: Exception?)
        fun onGetMessagesSuccessfully(messages: ArrayList<Message>)
        fun insertMessageSuccessfully()
        fun insertMessageFailure(exception: Exception?)
        fun getFriendInformationSuccessfully(user: User)
        fun showEmptyData()
    }

    interface Presenter : BasePresenter {
        fun getFriendInformation(idUser: String)
        fun compressBitmap(inputStream: InputStream?): ByteArray
        fun handleMessage(message: Message)
        fun onChatScreenVisible(isVisible: Boolean)
    }
}
