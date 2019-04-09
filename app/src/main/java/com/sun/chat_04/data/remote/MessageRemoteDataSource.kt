package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.repositories.MessageDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class MessageRemoteDataSource(
    auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    friend: Friend
) :
    MessageDataSource.Remote {

    private val uid = auth.currentUser?.uid.toString()
    private val uidUserRec = friend.idUser
    override fun insertMessage(message: Message, callback: RemoteCallback<Boolean>) {

        val userSendRef = "${Constants.MESSAGES}/$uid/$uidUserRec"
        val userRecRef = "${Constants.MESSAGES}/$uidUserRec/$uid"
        val idMessage = database.reference.child(userSendRef)
            .child(userRecRef).push().key
        val bodyMessage = HashMap<String, String>()
        bodyMessage[Constants.CONTENTS] = message.contents
        bodyMessage[Constants.FROM] = uid
        bodyMessage[Constants.ID_MESSAGE] = idMessage.toString()
        val detailMessage = HashMap<String, Any>()
        detailMessage["$userRecRef/$idMessage"] = bodyMessage
        detailMessage["$userSendRef/$idMessage"] = bodyMessage
        database.reference.updateChildren(detailMessage)
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
                            messages.add(messaage)
                            callback.onSuccessfuly(messages)
                        }
                    }
                }
            })
    }
}
