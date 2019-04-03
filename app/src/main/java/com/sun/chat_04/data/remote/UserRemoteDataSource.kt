package com.sun.chat_04.data.remote

import android.location.Location
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class UserRemoteDataSource(private val auth: FirebaseAuth, private val database: FirebaseDatabase) :
    UserDataSource.Remote {

    override fun loginByEmailAndPassword(email: String, password: String, callback: RemoteCallback<Boolean>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback.onSuccessfuly(true)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun handleFbLogin(
        token: AccessToken?,
        callbackLogin: RemoteCallback<Boolean>
    ) {
        val credential = token?.token?.let { FacebookAuthProvider.getCredential(it) }
        credential?.let {
            auth.signInWithCredential(it).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    callbackLogin.onFailure(task.exception)
                    return@addOnCompleteListener
                }
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val user = User(currentUser.uid, currentUser.displayName, isOnline = 1)
                    database.reference.child(Constants.USERS)
                        .child(user.idUser)
                        .setValue(user)
                        .addOnSuccessListener { callbackLogin.onSuccessfuly(true) }
                        .addOnFailureListener {
                            callbackLogin.onFailure(it)
                        }
                }

            }
        }
    }

    override fun insertUser(user: User, email: String?, password: String?, callback: RemoteCallback<Boolean>) {
        if (email != null && password != null) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    user.idUser = auth.currentUser?.uid.toString()
                    if (!it.isSuccessful) {
                        callback.onFailure(it.exception!!)
                        return@addOnCompleteListener
                    }
                    database.reference.child(Constants.USERS)
                        .child(user.idUser)
                        .setValue(user)
                        .addOnSuccessListener { callback.onSuccessfuly(true) }
                        .addOnFailureListener {
                            callback.onFailure(it)
                        }
                }
        }
    }

    override fun upgradeLocationUser(location: Location, callback: RemoteCallback<Boolean>) {
        if (auth.currentUser == null) {
            return
        }
        try {
            database.getReference(USERS).child(auth.currentUser?.uid.toString()).child(LATITUDE)
                .setValue(location.latitude)
            database.getReference(USERS).child(auth.currentUser?.uid.toString()).child(LONGITUDE)
                .setValue(location.longitude)
            callback.onSuccessfuly(true)
        } catch (databaseException: DatabaseException) {
            callback.onFailure(databaseException)
        }
    }

    companion object {
        private const val USERS = "Users"
        private const val LATITUDE = "lat"
        private const val LONGITUDE = "lgn"
    }
}
