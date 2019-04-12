package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.Message
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

    override fun updateImageMessage(message: Message, callback: RemoteCallback<Boolean>) {
        val idMessage = generateIdMesasge()
        val bytes = message.bytes
        uploadImage(bytes, object : RemoteCallback<String> {
            override fun onSuccessfuly(data: String) {
                insertMessages(
                    idMessage?.let {
                        Message(it, data, uid, Constants.IMAGE_MESSAGE)
                    }, callback
                )
            }

            override fun onFailure(exception: Exception?) {
                callback.onFailure(exception)
            }
        })
    }

    private fun generateIdMesasge(): String? {
        return database.reference.child(userSendRef)
            .child(userRecRef).push().key
    }

    override fun updateTextMessage(message: Message, callback: RemoteCallback<Boolean>) {
        val idMessage = generateIdMesasge()
        insertMessages(
            idMessage?.let {
                Message(it, message.contents, uid, Constants.TEXT_MESSAGE)
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

    private fun insertMessages(message: Message?, callback: RemoteCallback<Boolean>) {
        message?.let {
            val bodyMessage = HashMap<String, String>()
            bodyMessage[Constants.FROM] = uid
            bodyMessage[Constants.ID_MESSAGE] = it.id
            bodyMessage[Constants.CONTENTS] = it.contents
            bodyMessage[Constants.TYPE] = it.type
            val detailMessage = HashMap<String, Any>()
            detailMessage["$userRecRef/${it.id}"] = bodyMessage
            detailMessage["$userSendRef/${it.id}"] = bodyMessage
            database.reference.updateChildren(detailMessage)
                .addOnSuccessListener { callback.onSuccessfuly(true) }
                .addOnFailureListener { callback.onFailure(it) }
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
