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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sun.chat_04.R
import com.sun.chat_04.R.id
import com.sun.chat_04.R.layout
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.signup.SignUpActivity
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.activity_login.buttonLoginEmailPass
import kotlinx.android.synthetic.main.activity_login.buttonLoginFace
import kotlinx.android.synthetic.main.activity_login.buttonLoginFb
import kotlinx.android.synthetic.main.activity_login.editEmailLogin
import kotlinx.android.synthetic.main.activity_login.editPassLogin
import kotlinx.android.synthetic.main.activity_login.progressbarSignIn
import kotlinx.android.synthetic.main.activity_login.textSignUp
import java.lang.Exception

class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginContract.View, TextWatcher {

    private lateinit var presenter: LoginContract.Presenter
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)
        initComponents()
        initLoginFacebook()
    }

    private fun initComponents() {
        val auth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        presenter = LoginPresenter(UserRepository(UserRemoteDataSource(auth, firebaseDatabase)), this)
        buttonLoginFace.setOnClickListener(this)
        buttonLoginEmailPass.setOnClickListener(this)
        editPassLogin.addTextChangedListener(this)
        editEmailLogin.addTextChangedListener(this)
        textSignUp.setOnClickListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkValidateLogin()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            id.buttonLoginFace -> {
                buttonLoginFb.performClick()
            }
            id.buttonLoginEmailPass -> {
                loginWithEmailAndPass()
            }
            id.textSignUp -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onSaveSuccesfully() {
    }

    override fun onSaveFailure(exception: Exception?) {
    }

    override fun onGetTokenFail(exception: Exception?) {
        Toast.makeText(this, exception?.message, Toast.LENGTH_SHORT).show()
    }

    private fun loginWithEmailAndPass() {
        progressbarSignIn.visibility = View.VISIBLE
        val email = editEmailLogin.text.toString()
        val password = editPassLogin.text.toString()
        if (::presenter.isInitialized) {
            presenter.loginByEmailAndPassword(email, password)
        }
    }

    private fun checkValidateLogin() {
        val email = editEmailLogin.text.toString()
        val password = editPassLogin.text.toString()
        if (::presenter.isInitialized) {
            presenter.checkValidateLogin(email, password)
        }
    }

    override fun invalidLogin() {
        buttonLoginEmailPass.isClickable = false
        buttonLoginEmailPass.setBackgroundColor(
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.color_pink_light
            )
        )
    }

    override fun validLogin() {
        buttonLoginEmailPass.isClickable = true
        buttonLoginEmailPass.setBackgroundColor(
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.color_pink_dark
            )
        )
    }

    override fun onLoginSuccessfully() {
        progressbarSignIn.visibility = View.INVISIBLE
        Toast.makeText(this, R.string.login_successfully, Toast.LENGTH_SHORT).show()
    }

    override fun onLoginFailure(message: Int) {
        progressbarSignIn.visibility = View.INVISIBLE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initLoginFacebook() {
        callbackManager = CallbackManager.Factory.create()
        if (::callbackManager.isInitialized) {
            buttonLoginFb.setReadPermissions(Constants.PERMISSION_FB_EMAIL, Constants.PERMISSION_FB_PUBLIC_PROFILE)
            buttonLoginFb.registerCallback(callbackManager, presenter)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::callbackManager.isInitialized) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}
