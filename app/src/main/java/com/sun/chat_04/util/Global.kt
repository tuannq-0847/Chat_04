package com.sun.chat_04.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Global {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()
}
