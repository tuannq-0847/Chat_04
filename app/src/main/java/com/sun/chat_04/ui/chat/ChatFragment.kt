package com.sun.chat_04.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.remote.MessageRemoteDataSource
import com.sun.chat_04.data.repositories.MessageRepository
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_chat_message.buttonSend
import kotlinx.android.synthetic.main.fragment_chat_message.editMessage
import kotlinx.android.synthetic.main.fragment_chat_message.recyclerChat
import kotlinx.android.synthetic.main.fragment_chat_message.toolbarMsg

class ChatFragment : Fragment(), ChatContract.View, View.OnClickListener {
    private lateinit var adapterChat: AdapterChat
    private lateinit var chatPresenter: ChatPresenter

    override fun onGetMessagesSuccessfully(listMessage: ArrayList<Message>) {
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerChat?.let {
            it.layoutManager = linearLayoutManager
            adapterChat = AdapterChat(context, Global.firebaseAuth.uid.toString(), listMessage)
            it.adapter = adapterChat
            it.scrollToPosition(listMessage.size - 1)
        }
    }

    override fun onGetMessagesFailure(task: Exception?) {
        Toast.makeText(context, task?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> {
                val message = Message(Constants.NONE, editMessage.text.toString())
                chatPresenter.handleSendMessage(message)
                editMessage.setText(Constants.NONE)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        toolbarMsg.setNavigationIcon(R.drawable.back)
        toolbarMsg.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        buttonSend.setOnClickListener(this)
        val bundle = arguments
        bundle?.let {
            val lastMessage = it.getSerializable(Constants.LAST_MESSAGE)
            chatPresenter =
                ChatPresenter(
                    this,
                    MessageRepository(
                        MessageRemoteDataSource(
                            Global.firebaseAuth,
                            Global.firebaseDatabase, lastMessage as LastMessage
                        )
                    )
                )
            toolbarMsg.title = lastMessage.userName
            chatPresenter.getListMessage()
        }
    }
}