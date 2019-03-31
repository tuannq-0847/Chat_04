package com.sun.chat_04.data.remote

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserDataSource
import com.sun.chat_04.ui.login.RemoteCallBackLogin
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import java.lang.Exception

class UserRemoteDataSource(private val auth: FirebaseAuth, private val database: FirebaseDatabase) :
    UserDataSource.Remote {

    override fun handleFbLogin(
        token: AccessToken?, callback: RemoteCallback<Boolean>,
        callbackLogin: RemoteCallBackLogin
    ) {
        val credential = token?.token?.let { FacebookAuthProvider.getCredential(it) }
        credential?.let {
            auth.signInWithCredential(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callbackLogin.onLoginSuccessfully()
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val user = User(currentUser.uid, currentUser.displayName, isOnline = 1)
                        database.reference.child(Constants.USERS)
                            .child(user.idUser)
                            .setValue(user)
                            .addOnSuccessListener { callback.onSuccessfuly(true) }
                            .addOnFailureListener {
                                callback.onFailure(it)
                            }
                    }
                } else {
                    callbackLogin.onLoginFailure(task.exception)
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
                }
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
