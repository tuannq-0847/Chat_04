package com.sun.chat_04.ui.listchat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.remote.LastMessageRemoteDataSource
import com.sun.chat_04.data.repositories.LastMessageRepository
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_list_chat.progressLoadFriend
import kotlinx.android.synthetic.main.fragment_list_chat.recyclerListChat

class LastMessageFragment : Fragment(), LastMessageContract.View {
    private lateinit var adapter: LastMessageAdapter
    private lateinit var mLastMessagePre: LastMessageContract.Presenter
    override fun onGetLastMessagesSuccessfully(lastMessages: ArrayList<LastMessage>) {
        progressLoadFriend.visibility = View.INVISIBLE
        adapter = LastMessageAdapter(lastMessages) { lastMessage -> onClick(lastMessage) }
        recyclerListChat.layoutManager = LinearLayoutManager(context)
        if (::adapter.isInitialized) {
            recyclerListChat.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLastMessagePre = LastMessagePresenter(
            this, LastMessageRepository(
                LastMessageRemoteDataSource(
                    Global.firebaseAuth, Global.firebaseDatabase
                )
            )
        )
        if (::mLastMessagePre.isInitialized) {
            mLastMessagePre.getLastMessages()
        }
    }

    override fun onGetLastMessagesFailed(exception: Exception?) {
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun onClick(lastMessage: LastMessage) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_chat, container, false)
    }
}
