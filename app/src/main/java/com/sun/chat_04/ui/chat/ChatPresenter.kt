package com.sun.chat_04.ui.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.repositories.MessageRepository
import com.sun.chat_04.ui.signup.RemoteCallback

class ChatPresenter(
    private val view: ChatContract.View,
    private val repository: MessageRepository
) : ChatContract.Presenter {

    override fun getListMessage() {
        repository.getListMessage(object : RemoteCallback<ArrayList<com.sun.chat_04.data.model.Message>> {
            override fun onSuccessfuly(data: ArrayList<Message>) {
                view.onGetMessagesSuccessfully(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onGetMessagesFailure(exception)
            }
        })
    }

    override fun handleSendMessage(
        message: com.sun.chat_04.data.model.Message
    ) {
        repository.insertMsgToDb(message, object : RemoteCallback<Boolean> {
            override fun onSuccessfuly(data: Boolean) {
            }

            override fun onFailure(exception: Exception?) {
            }
        })
    }
}