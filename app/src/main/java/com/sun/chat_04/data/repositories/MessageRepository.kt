package com.sun.chat_04.data.repositories

import android.graphics.Bitmap
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.remote.MessageRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class MessageRepository(
    private val remoteDataSource: MessageRemoteDataSource
) : MessageDataSource.Remote {

    override fun getMessages(callback: RemoteCallback<ArrayList<Message>>) {
        remoteDataSource.getMessages(callback)
    }

    override fun handleMessage(message: Message, bitmap: Bitmap?, callback: RemoteCallback<Boolean>) {
        remoteDataSource.handleMessage(message, bitmap, callback)
    }
}

