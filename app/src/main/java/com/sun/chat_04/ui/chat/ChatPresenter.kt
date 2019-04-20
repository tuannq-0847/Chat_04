package com.sun.chat_04.ui.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.MessageRepository
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ChatPresenter(
    private val view: ChatContract.View,
    private val repository: MessageRepository,
    private val userRepository: UserRepository
) : ChatContract.Presenter {

    override fun getFriendInformation(idUser: String) {
        userRepository.getUserInfo(idUser, object : RemoteCallback<User> {
            override fun onSuccessfuly(data: User) {
                view.getFriendInformationSuccessfully(data)
                repository.getMessages(object : RemoteCallback<ArrayList<Message>> {
                    override fun onSuccessfuly(data: ArrayList<Message>) {
                        view.onGetMessagesSuccessfully(data)
                    }

                    override fun onFailure(exception: Exception?) {
                        view.onGetMessagesFailure(exception)
                    }
                })
            }

            override fun onFailure(exception: Exception?) {
            }
        })
    }

    override fun compressBitmap(inputStream: InputStream?): ByteArray {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, outputStream)
        return outputStream.toByteArray()
    }

    override fun handleMessage(
        message: Message
    ) {
        when (message.type) {
            Constants.TEXT_MESSAGE -> repository.updateTextMessage(
                message,
                object : RemoteCallback<Boolean> {
                    override fun onSuccessfuly(data: Boolean) {
                        view.insertMessageSuccessfully()
                    }

                    override fun onFailure(exception: Exception?) {
                        view.insertMessageFailure(exception)
                    }
                })
            Constants.IMAGE_MESSAGE -> repository.updateImageMessage(
                message,
                object : RemoteCallback<Boolean> {
                    override fun onSuccessfuly(data: Boolean) {
                        view.insertMessageSuccessfully()
                    }

                    override fun onFailure(exception: Exception?) {
                        view.insertMessageFailure(exception)
                    }
                })
        }
    }

    override fun onChatScreenVisible(isVisible: Boolean) {
        repository.updateSeenStatusFriend(isVisible)
    }
}
