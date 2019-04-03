package com.sun.chat_04.util

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Global {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val PERMISSIONS = arrayOf(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

    fun checkGrantedPermission(context: Context, index: Int): Boolean =
        ContextCompat.checkSelfPermission(context, PERMISSIONS[index]) == PackageManager.PERMISSION_GRANTED

    fun showMessage(context: Context?, msg: String) =
        context?.let { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
}
