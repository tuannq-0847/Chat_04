package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.ui.signup.RemoteCallback

interface LastMessageDataSource {
    interface Remote {
        fun getLastMessages(callback: RemoteCallback<ArrayList<LastMessage>>)
    }
}
