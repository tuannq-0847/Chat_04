package com.sun.chat_04.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserDataSource
import com.sun.chat_04.ui.signup.RemoteCallback

class UserRemoteDataSource(val auth: FirebaseAuth, val database: FirebaseDatabase) : UserDataSource.Remote {
    override fun insertUser(user: User, email: String, password: String, callback: RemoteCallback<Boolean>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                user.idUser = auth.currentUser?.uid.toString()
                if (!it.isSuccessful) {
                    callback.onFailure(it.exception!!)
                    return@addOnCompleteListener
                }
                database.reference.child(USERS)
                    .child(user.idUser)
                    .setValue(user)
                    .addOnSuccessListener { callback.onSuccessfuly(true) }
                    .addOnFailureListener {
                        callback.onFailure(it)
                    }
            }
    }

    companion object {
        private const val USERS = "Users"
    }
}
