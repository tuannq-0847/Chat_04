package com.sun.chat_04.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.signup.RemoteCallback

interface MessageDataSource {
    interface Remote {
        fun insertMsgToDb(message: Message, callback: RemoteCallback<Boolean>)
        fun getListMessage(callback: RemoteCallback<ArrayList<Message>>)
    }
}