package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.remote.MessageRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class MessageRepository(
    private val remoteDataSource: MessageRemoteDataSource
) : MessageDataSource.Remote {


    override fun getListMessage(callback: RemoteCallback<ArrayList<Message>>) {
        remoteDataSource.getListMessage(callback)
    }

    override fun insertMsgToDb(message: Message, callback: RemoteCallback<Boolean>) {
        remoteDataSource.insertMsgToDb(message, callback)
    }
}