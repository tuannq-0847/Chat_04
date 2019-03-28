package com.sun.chat_04.ui.login

import com.facebook.AccessToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import java.lang.Exception

class LoginPresenter(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val view: LoginContract.View
) : LoginContract.Presenter, OnSuccessListener<Void>
    , OnFailureListener {

    private val currentUser: FirebaseUser? = auth.currentUser

    override fun onSuccess(success: Void?) {
        view.onSaveListener(true)
    }

    override fun onFailure(exception: Exception) {
        view.onSaveListener(false)
    }

    override fun handleFbLogin(token: AccessToken?) {
        val credential = token?.token?.let { FacebookAuthProvider.getCredential(it) }
        credential?.let {
            auth.signInWithCredential(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToRemote()
                    view.onLoginListener(true)
                } else {
                    view.onLoginListener(false)
                }

            }
        }
    }

    override fun loginByEmailAndPassword(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToRemote()
                    view.onLoginListener(true)
                } else {
                    view.onLoginListener(false)
                }
            }
    }

    private fun saveUserToRemote() {
        if (currentUser != null) {
            val user = User(currentUser.uid, currentUser.displayName)
            val userRemoteDataSource = UserRemoteDataSource(database)
            userRemoteDataSource.saveUserToRemote(user, this, this)
        }
    }
}
