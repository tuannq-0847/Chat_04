package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.remote.MessageRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class MessageRepository(
    private val remoteDataSource: MessageRemoteDataSource
) : MessageDataSource.Remote {

    override fun updateSeenStatusFriend(isSeen: Boolean) {
        remoteDataSource.updateSeenStatusFriend(isSeen)
    }

    override fun onGetUserRecId(): String {
        return remoteDataSource.onGetUserRecId()
    }

    override fun updateImageMessage(message: Message, callback: RemoteCallback<Boolean>) {
        remoteDataSource.updateImageMessage(message, callback)
    }

    override fun updateTextMessage(message: Message, callback: RemoteCallback<Boolean>) {
        remoteDataSource.updateTextMessage(message, callback)
    }

    override fun getMessages(callback: RemoteCallback<ArrayList<Message>>) {
        remoteDataSource.getMessages(callback)
    }
}
