package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.data.repositories.MessageDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class MessageRemoteDataSource(
    auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val lastMessage: LastMessage
) :
    MessageDataSource.Remote {

    private val uid = auth.currentUser?.uid.toString()
    private var avatarLink: String? = null

    override fun insertMsgToDb(message: Message, callback: RemoteCallback<Boolean>) {
        val uidUserRec = lastMessage.idUser
        val userSendRef = "${Constants.MESSAGES}/$uid/$uidUserRec"
        val userRecRef = "${Constants.MESSAGES}/$uidUserRec/$uid"
        val notificationRef = "${Constants.NOTIFICATIONS}/$uidUserRec"
        val userMsg = database.reference.child(userSendRef)
            .child(userRecRef).push()
        val idMsg = userMsg.key
        val notificationId = database.reference.child(notificationRef).push()
        database.reference.child(notificationRef).child(notificationId.key.toString())
            .child(Constants.CONTENTS)
            .setValue(message.contents)
        val bodyMessage = HashMap<String, String>()
        bodyMessage[Constants.CONTENTS] = message.contents
        bodyMessage[Constants.FROM] = uid
        bodyMessage[Constants.ID_MSG] = idMsg.toString()
        val detailMessage = HashMap<String, Any>()
        detailMessage["$userRecRef/$idMsg"] = bodyMessage
        detailMessage["$userSendRef/$idMsg"] = bodyMessage
        database.reference.updateChildren(detailMessage)
        getInformationUser()
    }

    fun getInformationUser() {
        database.reference.child(Constants.USERS)
            .child(uid).child("pathAvatar").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    avatarLink = dataSnapshot.getValue(String::class.java)
                }
            })
    }

    override fun getListMessage(callback: RemoteCallback<ArrayList<Message>>) {
        val uidUserRec = lastMessage.idUser
        val listMsg: ArrayList<Message> = ArrayList()
        database.reference.child(Constants.MESSAGES).child(uid).child(uidUserRec)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(data: DataSnapshot) {
                    listMsg.clear()
                    for (dataSnapshot in data.children) {
                        val messaage = dataSnapshot.getValue(Message::class.java)
                        listMsg.add(messaage!!)
                        callback.onSuccessfuly(listMsg)
                    }
                }
            })
    }
}