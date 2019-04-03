package com.sun.chat_04.ui.listchat.search

import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.BasePresenter
import com.sun.chat_04.ui.BaseView
import java.lang.Exception

interface SearchConstract {
    interface View : BaseView {
        fun onGetListUserSuccesfully(lastMessages: ArrayList<LastMessage>)
        fun onGetListUserFailure(exception: Exception?)
    }

    interface Presenter : BasePresenter {
        fun getListUserFromSearcjQuerry(query: String)
    }
}