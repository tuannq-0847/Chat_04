package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.model.Notification
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.MessageDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import java.text.DateFormat
import java.util.Date

class MessageRemoteDataSource(
    auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    friend: Friend,
    private val storage: FirebaseStorage
) :
    MessageDataSource.Remote {

    private val uid = auth.currentUser?.uid.toString()
    private val uidUserRec = friend.idUser
    private val userSendRef = "${Constants.MESSAGES}/$uid/$uidUserRec"
    private val userRecRef = "${Constants.MESSAGES}/$uidUserRec/$uid"
    private var isVisible = false

    override fun updateImageMessage(message: Message, callback: RemoteCallback<Boolean>) {
        val idMessage = generateIdMessage()
        val bytes = message.bytes
        uploadImage(bytes, object : RemoteCallback<String> {
            override fun onSuccessfuly(data: String) {
                insertMessages(
                    idMessage?.let {
                        Message(
                            it, data, uid, Constants.IMAGE_MESSAGE, seen = Constants.SEEN,
                            avatar = Constants.NONE
                        )
                    }, callback
                )
            }

            override fun onFailure(exception: Exception?) {
                callback.onFailure(exception)
            }
        })
    }

    override fun updateSeenStatusFriend(isSeen: Boolean) {
        val friendSendRef = "${Constants.FRIENDS}/$uid/$uidUserRec"
        val status = when (isSeen) {
            true -> Constants.SEEN
            else -> Constants.NOT_SEEN
        }
        database.reference
            .child(friendSendRef)
            .child(Constants.MESSAGE_SEEN)
            .setValue(status)
    }

    private fun generateIdMessage(): String? {
        return database.reference.child(userSendRef)
            .child(userRecRef).push().key
    }

    override fun updateTextMessage(message: Message, callback: RemoteCallback<Boolean>) {
        val idMessage = generateIdMessage()
        insertMessages(
            idMessage?.let {
                Message(
                    it, message.contents, uid, Constants.TEXT_MESSAGE, seen = Constants.SEEN,
                    avatar = Constants.NONE
                )
            }, callback
        )
    }

    private fun uploadImage(bytes: ByteArray?, callback: RemoteCallback<String>) {
        val pathImage = DateFormat.getDateTimeInstance().format(Date())
        val ref = storage.reference.child(Constants.MESSAGES)
            .child(pathImage)
        bytes?.let {
            ref.putBytes(bytes)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener { uri ->
                            callback.onSuccessfuly(uri.toString())
                        }
                        .addOnFailureListener { callback.onFailure(it) }
                }
                .addOnFailureListener { callback.onFailure(it) }
        }
    }

    override fun onGetUserRecId(): String {
        return uidUserRec
    }

    private fun insertMessages(message: Message?, callback: RemoteCallback<Boolean>) {
        message?.let { mess ->
            val idNotification = database.reference
                .child(Constants.NOTIFICATION)
                .child(uidUserRec).push().key
            database.reference
                .child("${Constants.NOTIFICATION}/$uidUserRec/$idNotification")
                .setValue(Notification(mess.contents, uid))
            val bodyMessage = HashMap<String, Any>()
            bodyMessage[Constants.FROM] = uid
            bodyMessage[Constants.ID_MESSAGE] = mess.id
            bodyMessage[Constants.CONTENTS] = mess.contents
            bodyMessage[Constants.TYPE] = mess.type
            bodyMessage[Constants.MESSAGE_SEEN] = mess.seen
            bodyMessage[Constants.AVATAR] = mess.avatar
            val detailMessage = HashMap<String, Any>()
            detailMessage["$userRecRef/${mess.id}"] = bodyMessage
            detailMessage["$userSendRef/${mess.id}"] = bodyMessage
            database.reference.updateChildren(detailMessage)
                .addOnSuccessListener {
                    saveLastMessage(mess, callback)
                }
                .addOnFailureListener { callback.onFailure(it) }
        }
    }

    private fun saveLastMessage(message: Message?, callback: RemoteCallback<Boolean>) {
        message?.let {
            val friendSendRef = "${Constants.FRIENDS}/$uid/$uidUserRec"
            val friendRecRef = "${Constants.FRIENDS}/$uidUserRec/$uid"
            updateSenderStatusMessage(message, friendSendRef, callback)
            updateReceiverStatusMessage(message, friendRecRef, callback)
        }
    }

    private fun updateReceiverStatusMessage(
        message: Message,
        friendRecRef: String,
        callback: RemoteCallback<Boolean>
    ) {
        database.reference.child(friendRecRef)
            .child(Constants.CONTENTS)
            .setValue(message.contents)
            .addOnSuccessListener {
                database.reference.child(friendRecRef)
                    .child(Constants.MESSAGE_SEEN)
                    .setValue(Constants.NOT_SEEN)
                    .addOnSuccessListener {
                        callback.onSuccessfuly(true)
                    }
                    .addOnFailureListener {
                        callback.onFailure(it)
                    }
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    private fun updateSenderStatusMessage(
        message: Message,
        friendSendRef: String,
        callback: RemoteCallback<Boolean>
    ) {
        database.reference.child(friendSendRef)
            .child(Constants.CONTENTS)
            .setValue(message.contents)
            .addOnSuccessListener {
                database.reference.child(friendSendRef)
                    .child(Constants.MESSAGE_SEEN)
                    .setValue(Constants.SEEN)
                    .addOnSuccessListener {
                        callback.onSuccessfuly(true)
                    }
                    .addOnFailureListener {
                        callback.onFailure(it)
                    }
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun getMessages(callback: RemoteCallback<ArrayList<Message>>) {
        val messages: ArrayList<Message> = ArrayList()
        database.reference.child(Constants.MESSAGES).child(uid).child(uidUserRec)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.toException())
                }

                override fun onDataChange(data: DataSnapshot) {
                    messages.clear()
                    if (!data.hasChildren()) {
                        callback.onSuccessfuly(messages)
                    }
                    for (dataSnapshot in data.children) {
                        val messaage = dataSnapshot.getValue(Message::class.java)
                        messaage?.let {
                            messages.add(it)
                            callback.onSuccessfuly(messages)
                        }
                    }
                }
            })
    }
}
