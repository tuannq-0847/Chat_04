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
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.repositories.UserDataSource
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Constants
import java.util.Date

class UserRemoteDataSource(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) :
    UserDataSource.Remote {

    override fun loginByEmailAndPassword(email: String?, password: String?, callback: RemoteCallback<Boolean>) {
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        updateUserStatus(it, Constants.ONLINE, callback)
                    }

                }
                .addOnFailureListener {
                    callback.onFailure(it)
                }
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
                    currentUser.displayName?.let { name ->
                        updateUserStatus(currentUser.uid, Constants.ONLINE, callbackLogin)
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
                    user.online = Constants.ONLINE
                    if (!it.isSuccessful) {
                        callback.onFailure(it.exception!!)
                        return@addOnCompleteListener
                    }
                    getInstanceIdUser(object : RemoteCallback<String> {
                        override fun onSuccessfuly(data: String) {
                            user.devicetoken = data
                            database.reference.child(Constants.USERS)
                                .child(user.idUser)
                                .setValue(user)
                                .addOnSuccessListener { callback.onSuccessfuly(true) }
                                .addOnFailureListener {
                                    callback.onFailure(it)
                                }
                        }

                        override fun onFailure(exception: Exception?) {
                            callback.onFailure(exception)
                        }
                    })

                }
        }
    }

    private fun getInstanceIdUser(callback: RemoteCallback<String>) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    callback.onSuccessfuly(token.toString())
                } else {
                    callback.onFailure(task.exception)
                }
            }
    }

    override fun upgradeLocationUser(location: Location, callback: RemoteCallback<Boolean>) {
        if (auth.currentUser == null) {
            return
        }
        try {
            database.getReference(Constants.USERS).child(auth.currentUser?.uid.toString()).child(LATITUDE)
                .setValue(location.latitude)
            database.getReference(Constants.USERS).child(auth.currentUser?.uid.toString()).child(LONGITUDE)
                .setValue(location.longitude)
            callback.onSuccessfuly(true)
        } catch (databaseException: DatabaseException) {
            callback.onFailure(databaseException)
        }
    }

    override fun getUsers(callback: RemoteCallback<List<User>>) {
        database.getReference(Constants.USERS).addValueEventListener(object : ValueEventListener {
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
                        user?.let { users.add(it) }
                    }
                    callback.onSuccessfuly(users)
                }
            }
        })
    }

    override fun getUserInfo(userId: String, callback: RemoteCallback<User>) {
        database.getReference(Constants.USERS).child(userId)
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
        val pathImage = "$userId/${uri.path}"
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
    }

    override fun insertUserImagePath(userId: String, uri: Uri, field: String, callback: RemoteCallback<Uri>) {
        database.getReference(Constants.USERS)
            .child(userId)
            .child(field)
            .setValue(uri.toString())
            .addOnSuccessListener {
                updateUserImages(userId, uri, callback)
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

    override fun checkIsFriend(userId: String, friendId: String, callback: RemoteCallback<Boolean>) {
        database.getReference(Constants.FRIENDS)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    callback.onFailure(databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    when {
                        !dataSnapshot.child(userId).exists() -> callback.onSuccessfuly(false)
                        !dataSnapshot.child(userId).child(friendId).exists() -> callback.onSuccessfuly(false)
                        else -> callback.onSuccessfuly(true)
                    }
                }
            })
    }

    override fun checkInvitedMoreFriends(userId: String, friendId: String, callback: RemoteCallback<Boolean>) {
        database.getReference(Constants.REQUEST_FRIEND)
            .child(friendId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    callback.onFailure(databaseError.toException())
                }

                override fun onDataChange(databasSnapshot: DataSnapshot) {
                    if (!databasSnapshot.child(userId).exists()) {
                        callback.onSuccessfuly(false)
                    } else {
                        callback.onSuccessfuly(true)
                    }
                }
            })
    }

    override fun inviteMoreFriend(userId: String, friendId: String, callback: RemoteCallback<Boolean>) {
        database.getReference(Constants.REQUEST_FRIEND)
            .child(friendId)
            .child(userId)
            .setValue(userId)
            .addOnSuccessListener {
                callback.onSuccessfuly(true)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun cancelInviteMoreFriends(userId: String, friendId: String, callback: RemoteCallback<Boolean>) {
        database.getReference(Constants.REQUEST_FRIEND)
            .child(friendId)
            .child(userId)
            .removeValue()
            .addOnSuccessListener {
                callback.onSuccessfuly(true)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun updateUserStatus(userId: String, isOnline: Int, callback: RemoteCallback<Boolean>) {
        getInstanceIdUser(object : RemoteCallback<String> {
            override fun onFailure(exception: Exception?) {
                callback.onFailure(exception)
            }

            override fun onSuccessfuly(data: String) {
                database.reference.child(Constants.USERS)
                    .child(userId)
                    .child(ONLINE)
                    .setValue(isOnline)
                database.reference.child(Constants.USERS)
                    .child(userId)
                    .child(DEVICE_TOKEN)
                    .setValue(data)
                    .addOnSuccessListener { callback.onSuccessfuly(true) }
                    .addOnFailureListener { callback.onFailure(it) }
            }
        })
    }

    override fun updateUserImages(userId: String, uri: Uri, callback: RemoteCallback<Uri>) {
        val timestamp = Date().time
        database.getReference(Constants.IMAGES)
            .child(userId)
            .child(timestamp.toString())
            .setValue(uri.toString())
            .addOnSuccessListener {
                callback.onSuccessfuly(uri)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    override fun getUserImages(userId: String, callback: RemoteCallback<List<String>>) {
        database.getReference(Constants.IMAGES)
            .child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    callback.onFailure(databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.run {
                        val images = ArrayList<String>()
                        for (data in children) {
                            val image = data.getValue(String::class.java)
                            image?.let { images.add(it) }
                        }
                        callback.onSuccessfuly(images)
                    }
                }
            })
    }

    companion object {
        private const val LATITUDE = "lat"
        private const val LONGITUDE = "lgn"
        private const val ONLINE = "online"
        private const val DEVICE_TOKEN = "devicetoken"
    }
}
