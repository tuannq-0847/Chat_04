package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.repositories.LastMessageDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global

class LastMessageRemoteDataSource(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : LastMessageDataSource.Remote {

    override fun getLastMessages(callback: RemoteCallback<ArrayList<LastMessage>>) {
        val LastMessages: ArrayList<LastMessage> = ArrayList()
        val currentUserUid = auth.currentUser?.uid
        currentUserUid?.let {
            database.reference.child(Constants.FRIENDS)
                .child(it).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        LastMessages.clear()
                        for (data in dataSnapshot.children) {
                            val lastMessage = data.getValue(LastMessage::class.java)
                            lastMessage?.let {
                                LastMessages.add(it)
                                callback.onSuccessfuly(LastMessages)
                            }
                        }
                    }
                })
        }
    }
}
