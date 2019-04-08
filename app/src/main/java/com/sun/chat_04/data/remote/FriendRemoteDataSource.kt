package com.sun.chat_04.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.repositories.FriendDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class FriendRemoteDataSource(
    private val database: FirebaseDatabase
) : FriendDataSource.Remote {

    override fun getFriends(userId: String?, callback: RemoteCallback<ArrayList<Friend>>) {
        val Friends: ArrayList<Friend> = ArrayList()
        userId?.let {
            database.reference.child(Constants.FRIENDS)
                .child(it).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (!dataSnapshot.hasChildren()) {
                            callback.onFailure(null)
                        }
                        Friends.clear()
                        for (data in dataSnapshot.children) {
                            val lastMessage = data.getValue(Friend::class.java)
                            lastMessage?.let {
                                Friends.add(it)
                                callback.onSuccessfuly(Friends)
                            }
                        }
                    }
                })
        }
    }
}
