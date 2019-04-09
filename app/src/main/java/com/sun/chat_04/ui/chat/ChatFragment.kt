package com.sun.chat_04.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.remote.MessageRemoteDataSource
import com.sun.chat_04.data.repositories.MessageRepository
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_chat_message.buttonSend
import kotlinx.android.synthetic.main.fragment_chat_message.editMessage
import kotlinx.android.synthetic.main.fragment_chat_message.recyclerChat
import kotlinx.android.synthetic.main.fragment_chat_message.toolbarMessage

class ChatFragment : Fragment(), ChatContract.View, View.OnClickListener {
    private lateinit var adapter: ChatAdapter
    private lateinit var presenter: ChatPresenter

    override fun onGetMessagesSuccessfully(messages: ArrayList<Message>) {
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerChat.layoutManager = linearLayoutManager
        adapter = ChatAdapter(Global.firebaseAuth.currentUser?.uid, messages)
        if (::adapter.isInitialized) {
            recyclerChat.adapter = adapter
            recyclerChat.scrollToPosition(messages.size - INDEX_MESSAGES_1)
        }
    }

    override fun onGetMessagesFailure(task: Exception?) {
        Toast.makeText(context, task?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> {
                val message = Message(Constants.NONE, editMessage.text.toString())
                if (::presenter.isInitialized) {
                    presenter.handleSendMessage(message)
                    editMessage.setText(Constants.NONE)
                }
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
        toolbarMessage.setNavigationIcon(R.drawable.back)
        toolbarMessage.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        buttonSend.setOnClickListener(this)
        val bundle = arguments
        bundle?.let {
            val friend = it.getParcelable<Friend>(Constants.ARGUMENT_FRIENDS)
            friend?.let { friends ->
                presenter =
                    ChatPresenter(
                        this,
                        MessageRepository(
                            MessageRemoteDataSource(
                                Global.firebaseAuth,
                                Global.firebaseDatabase, friends
                            )
                        )
                    )
                toolbarMessage.title = friends.userName
                if (::presenter.isInitialized) {
                    presenter.getMessages()
                }
            }

        }
    }

    companion object {
        const val INDEX_MESSAGES_1 = 1
    }
}
