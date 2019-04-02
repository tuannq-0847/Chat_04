package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.remote.LastMessageRemoteDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class LastMessageRepository(private val remote: LastMessageRemoteDataSource) : LastMessageDataSource.Remote {
    override fun getLastMessages(callback: RemoteCallback<ArrayList<LastMessage>>) {
        remote.getLastMessages(callback)
    }
}
