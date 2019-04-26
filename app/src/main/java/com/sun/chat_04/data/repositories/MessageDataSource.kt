package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.signup.RemoteCallback

interface MessageDataSource {
    interface Remote {
        fun updateImageMessage(message: Message, callback: RemoteCallback<Boolean>)
        fun updateTextMessage(message: Message, callback: RemoteCallback<Boolean>)
        fun getMessages(callback: RemoteCallback<ArrayList<Message>>)
        fun updateSeenStatusFriend(isSeen: Boolean)
        fun onGetUserRecId(): String
    }
}
