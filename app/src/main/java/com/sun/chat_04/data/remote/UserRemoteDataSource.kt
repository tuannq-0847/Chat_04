package com.sun.chat_04.data.remote

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.User
import com.sun.chat_04.util.Constants

class UserRemoteDataSource(
    private val database: FirebaseDatabase
) {

    fun saveUserToRemote(
        user: User, onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        database.getReference("${Constants.USERS}/${user.idUser}")
            .setValue(user)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }
}
