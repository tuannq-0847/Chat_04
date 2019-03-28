package com.sun.chat_04.ui.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.R
import com.sun.chat_04.R.id
import com.sun.chat_04.R.layout
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.activity_login.btnLoginEmailPass
import kotlinx.android.synthetic.main.activity_login.btnLoginFace
import kotlinx.android.synthetic.main.activity_login.btnLoginFb
import kotlinx.android.synthetic.main.activity_login.edtEmailLogin
import kotlinx.android.synthetic.main.activity_login.edtPassLogin
import kotlinx.android.synthetic.main.activity_login.pgbSignIn

class LoginActivity : AppCompatActivity(), View.OnClickListener
    , LoginContract.View, TextWatcher {

    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)
        initComponents()
        initLoginFacebook()
    }

    private fun checkButton() {
        if (isValidEmailAndPassword()) {
            btnLoginEmailPass.setBackgroundColor(
                ContextCompat.getColor(
                    this@LoginActivity,
                    R.color.colorPinkDark
                )
            )
            btnLoginEmailPass.isClickable = true
        } else {
            btnLoginEmailPass.isClickable = false
            btnLoginEmailPass.setBackgroundColor(
                ContextCompat.getColor(
                    this@LoginActivity,
                    R.color.colorPinkLight
                )
            )
        }
    }

    private fun initComponents() {
        val auth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        presenter = LoginPresenter(auth, firebaseDatabase, this)
        btnLoginFace.setOnClickListener(this)
        btnLoginEmailPass.setOnClickListener(this)
        edtPassLogin.addTextChangedListener(this)
        edtEmailLogin.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkButton()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            id.btnLoginFace -> {
                btnLoginFb.performClick()
            }
            id.btnLoginEmailPass -> {
                loginWithEmailAndPass()
            }
        }
    }

    private fun loginWithEmailAndPass() {
        if (isValidEmailAndPassword()) {
            pgbSignIn.visibility = View.VISIBLE
            val email = edtEmailLogin.text.toString()
            val password = edtPassLogin.text.toString()
            if (::presenter.isInitialized) {
                presenter.loginByEmailAndPassword(email, password)
            }
        }
    }

    private fun isValidEmailAndPassword(): Boolean {
        return edtEmailLogin.text.isNotEmpty() && edtPassLogin.text.isNotEmpty()
    }

    override fun onLoginListener(isSuccessful: Boolean) {
        pgbSignIn.visibility = View.INVISIBLE
        if (isSuccessful) {
            Toast.makeText(this, R.string.login_successfully, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveListener(isSuccessful: Boolean) {
    }

    private fun initLoginFacebook() {
        val callbackManager = CallbackManager.Factory.create()
        btnLoginFb.setReadPermissions(Constants.PERMISSION_FB_EMAIL, Constants.PERMISSION_FB_PUBLIC_PROFILE)
        btnLoginFb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (::presenter.isInitialized) {
                    presenter.handleFbLogin(result?.accessToken)
                }
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
