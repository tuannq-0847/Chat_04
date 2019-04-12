package com.sun.chat_04.ui.chat

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import kotlinx.android.synthetic.main.fragment_chat_message.imageAdd
import kotlinx.android.synthetic.main.fragment_chat_message.recyclerChat
import kotlinx.android.synthetic.main.fragment_chat_message.toolbarMessage

class ChatFragment : Fragment(), ChatContract.View, View.OnClickListener {

    private lateinit var adapter: ChatAdapter
    private lateinit var presenter: ChatContract.Presenter

    companion object {
        @JvmStatic
        fun newInstance(friend: Friend) = ChatFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.ARGUMENT_FRIENDS, friend)
            }
        }

        const val REQUEST_CODE_PICK_IMAGE = 3000
        const val INDEX_MESSAGES_1 = 1
    }

    override fun onGetMessagesSuccessfully(messages: ArrayList<Message>) {
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerChat?.let {
            it.layoutManager = linearLayoutManager
            adapter = ChatAdapter(Global.firebaseAuth.currentUser?.uid, messages)
            if (::adapter.isInitialized) {
                it.adapter = adapter
                it.scrollToPosition(messages.size - INDEX_MESSAGES_1)
            }
        }
    }

    override fun onGetMessagesFailure(task: Exception?) {
        Toast.makeText(context, task?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> {
                val message =
                    Message(Constants.NONE, contents = editMessage.text.toString(), type = Constants.TEXT_MESSAGE)
                if (::presenter.isInitialized) {
                    presenter.handleMessage(message)
                    editMessage.setText(Constants.NONE)
                }
            }
            R.id.imageAdd -> {
                val intent = Intent()
                intent.action = android.content.Intent.ACTION_GET_CONTENT
                intent.type = Constants.IMAGE_GALERY
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        resources.getString(R.string.choose_image)
                    ),
                    REQUEST_CODE_PICK_IMAGE
                )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_message, container, false)
    }

    override fun insertMessageSuccessfully() {
    }

    override fun insertMessageFailure(exception: Exception?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)
        if (resultCode != RESULT_OK || requestCode != REQUEST_CODE_PICK_IMAGE || resultIntent == null) {
            return
        }
        val uriImage = resultIntent.data
        uriImage?.let {
            val inputStream = activity?.contentResolver?.openInputStream(it)
            if (::presenter.isInitialized) {
                val message = Message(
                    Constants.NONE,
                    type = Constants.IMAGE_MESSAGE,
                    contents = it.toString(),
                    bytes = presenter.compressBitmap(inputStream)
                )
                presenter.handleMessage(message)
            }
        }
    }

    private fun initComponents() {
        toolbarMessage.setNavigationIcon(R.drawable.back)
        toolbarMessage.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        buttonSend.setOnClickListener(this)
        imageAdd.setOnClickListener(this)
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
                                Global.firebaseDatabase, friends,
                                Global.firebaseStorage
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
}
