package com.sun.chat_04.data.remote

import android.location.Location
import android.net.Uri
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants

class UserRemoteDataSource(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) :
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

    override fun getUsers(callback: RemoteCallback<List<User>>) {
        database.getReference(USERS).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.run {
                    val users = ArrayList<User>()
                    for (data in children) {
                        val user = data.getValue(User::class.java)
                        if (user?.idUser.equals(auth.currentUser?.uid))
                            continue
                        user?.let { users.add(user) }
                    }
                    callback.onSuccessfuly(users)
                }
            }
        })
    }

    override fun getUserInfo(userId: String, callback: RemoteCallback<User>) {
        database.getReference(USERS).child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let { callback.onSuccessfuly(user) }
                }
            })
    }

    override fun insertUserImage(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>) {
        val pathImage = "${userId}/${uri.path}"
        val storageRef = firebaseStorage.getReference(pathImage)
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        insertUserImagePath(userId, it, field, callback)
                    }
                    .addOnFailureListener {
                        callback.onFailure(it)
                    }
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun insertUserImagePath(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>) {
        database.getReference(Constants.USERS)
            .child(userId)
            .child(field)
            .setValue(uri.toString())
            .addOnSuccessListener {
                callback.onSuccessfuly(uri)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun editUserProfile(user: User, callback: RemoteCallback<Boolean>) {
        try {
            database.getReference(Constants.USERS)
                .child(user.idUser)
                .child(Constants.USER_NAME)
                .setValue(user.userName)
            database.getReference(Constants.USERS)
                .child(user.idUser)
                .child(Constants.USER_AGE)
                .setValue(user.birthday)
            database.getReference(Constants.USERS)
                .child(user.idUser)
                .child(Constants.USER_GENDER)
                .setValue(user.gender)
            database.getReference(Constants.USERS)
                .child(user.idUser)
                .child(Constants.USER_BIO)
                .setValue(user.bio)
            callback.onSuccessfuly(true)
        } catch (databaseException: DatabaseException) {
            callback.onFailure(databaseException)
        }
    }

    companion object {
        private const val USERS = "Users"
        private const val USER_NAME = "userName"
        private const val LATITUDE = "lat"
        private const val LONGITUDE = "lgn"
        private const val USER_ID = "idUser"
    }
}
