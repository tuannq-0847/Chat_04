package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.FriendRequestDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class FriendRequestRemoteDataSource(
    auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : FriendRequestDataSource.Remote {

    private val currentUserId = auth.currentUser?.uid
    private val friendRef = database.reference.child(Constants.FRIENDS)

    override fun showFriendRequests(callback: RemoteCallback<ArrayList<User>>) {
        val friendRequests: ArrayList<User> = ArrayList()
        currentUserId?.let {
            database.reference.child(Constants.REQUEST_FRIEND)
                .child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        friendRequests.clear()
                        if (!dataSnapshot.hasChildren()) {
                            callback.onSuccessfuly(friendRequests)
                        }
                        for (data in dataSnapshot.children) {
                            val friendId = data.getValue(String::class.java)
                            friendId?.let {
                                getFriendRequests(it, friendRequests, callback)
                            }
                        }
                    }
                })
        }
    }

    private fun getFriendRequests(
        friendId: String,
        friendRequests: ArrayList<User>,
        callback: RemoteCallback<ArrayList<User>>
    ) {
        database.reference
            .child(Constants.USERS)
            .child(friendId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    friendRequests.clear()
                    val friendsRequest = dataSnapshot.getValue(User::class.java)
                    friendsRequest?.let {
                        friendRequests.add(it)
                        callback.onSuccessfuly(friendRequests)
                    }
                }
            })
    }

    override fun cancelFriendRequest(user: User, callback: RemoteCallback<String>) {
        deleteRequest(user, callback)
    }

    private fun deleteRequest(user: User, callback: RemoteCallback<String>) {
        currentUserId?.let {
            database.reference.child(Constants.REQUEST_FRIEND)
                .child(currentUserId).child(user.idUser)
                .removeValue()
                .addOnSuccessListener {
                    callback.onSuccessfuly(user.userName)
                }
                .addOnFailureListener {
                    callback.onFailure(it)
                }
        }
    }

    override fun approveFriendRequest(user: User, callback: RemoteCallback<String>) {
        user.userName.let {
            val friend = Friend(
                user.idUser, user.pathAvatar, user.online
                , contents = Constants.NONE, userName = it
            )
            insertFriend(friend, callback)
            getInformationUser(user, callback)
        }
    }

    private fun getInformationUser(user: User, callback: RemoteCallback<String>) {

        currentUserId?.let { uid ->
            database.reference.child(Constants.USERS)
                .child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val mainUser = dataSnapshot.getValue(User::class.java)
                        mainUser?.let { userMain ->
                            userMain.userName.let {
                                val friendInvert = Friend(
                                    mainUser.idUser, mainUser.pathAvatar, mainUser.online
                                    , contents = Constants.NONE, userName = it
                                )
                                insertInvertFriend(user.idUser, friendInvert, callback)
                            }

                        }
                    }
                })
            deleteRequest(user, callback)
        }
    }

    private fun insertInvertFriend(idUser: String, friend: Friend, callback: RemoteCallback<String>) {
        currentUserId?.let {
            friendRef.child(idUser)
                .child(it)
                .setValue(friend)
                .addOnSuccessListener {
                    callback.onSuccessfuly(Constants.NONE)
                }
                .addOnFailureListener {
                    callback.onFailure(it)
                }
        }
    }

    private fun insertFriend(friend: Friend, callback: RemoteCallback<String>) {
        currentUserId?.let {
            friendRef.child(it)
                .child(friend.idUser)
                .setValue(friend)
                .addOnSuccessListener { callback.onSuccessfuly(friend.userName) }
                .addOnFailureListener {
                    callback.onFailure(it)
                }
        }
    }
}
