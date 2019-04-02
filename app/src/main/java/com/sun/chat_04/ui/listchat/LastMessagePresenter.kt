package com.sun.chat_04.ui.listchat

import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.repositories.LastMessageRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import java.lang.Exception

class LastMessagePresenter(
    private val view: LastMessageContract.View,
    private val repository: LastMessageRepository
) : LastMessageContract.Presenter {

    override fun getLastMessages() {
        repository.getLastMessages(object : RemoteCallback<ArrayList<LastMessage>> {
            override fun onSuccessfuly(data: ArrayList<LastMessage>) {
                view.onGetLastMessagesSuccessfully(data)
            }

            override fun onFailure(exception: Exception?) {
                view.onGetLastMessagesFailed(exception)
            }
        })
    }
}
