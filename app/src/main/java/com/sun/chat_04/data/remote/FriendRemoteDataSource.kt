package com.sun.chat_04.data.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.FriendDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class FriendRemoteDataSource(
    private val database: FirebaseDatabase
) : FriendDataSource.Remote {

    override fun getFriends(userId: String?, callback: RemoteCallback<ArrayList<Friend>>) {
        val friends: ArrayList<Friend> = ArrayList()
        userId?.let {
            database.reference.child(Constants.FRIENDS)
                .child(it).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        friends.clear()
                        if (!dataSnapshot.hasChildren()) {
                            callback.onSuccessfuly(friends)
                        }
                        for (data in dataSnapshot.children) {
                            val friend = data.getValue(Friend::class.java)
                            friend?.let { fr ->
                                friends.add(fr)
                                callback.onSuccessfuly(friends)
                            }
                        }
                    }
                })
        }
    }
}
