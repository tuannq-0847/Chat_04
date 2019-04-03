package com.sun.chat_04.ui.listchat.search

import android.util.Log
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.repositories.LastMessageRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class SearchPresenter(
    private val view: SearchConstract.View,
    private val repository: LastMessageRepository
) : SearchConstract.Presenter {

    override fun getListUserFromSearcjQuerry(query: String) {
        val lastMessages: ArrayList<LastMessage> = ArrayList()
        repository.getLastMessages(object : RemoteCallback<ArrayList<LastMessage>> {
            override fun onSuccessfuly(data: ArrayList<LastMessage>) {
                lastMessages.clear()
                for (element in data) {
                    if (element.userName.toLowerCase().contains(query.toLowerCase())) {
                        lastMessages.add(element)
                    }
                }
                if (query == Constants.NONE) {
                    lastMessages.clear()
                }
                view.onGetListUserSuccesfully(lastMessages)
            }

            override fun onFailure(exception: Exception?) {
                view.onGetListUserFailure(exception)
            }
        })
    }
}