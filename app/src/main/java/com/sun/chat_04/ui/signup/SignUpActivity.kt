package com.sun.chat_04.ui.signup

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.sun.chat_04.R
import com.sun.chat_04.R.color
import com.sun.chat_04.R.id
import com.sun.chat_04.R.layout
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import kotlinx.android.synthetic.main.sign_up_screen.buttonSignUp
import kotlinx.android.synthetic.main.sign_up_screen.editConfirmPassword
import kotlinx.android.synthetic.main.sign_up_screen.editEmail
import kotlinx.android.synthetic.main.sign_up_screen.editFullname
import kotlinx.android.synthetic.main.sign_up_screen.editPassword
import kotlinx.android.synthetic.main.sign_up_screen.progress
import kotlinx.android.synthetic.main.sign_up_screen.radioGroups
import kotlinx.android.synthetic.main.sign_up_screen.textBirthday
import kotlinx.android.synthetic.main.sign_up_screen.toolbar
import java.util.Calendar

class SignUpActivity : AppCompatActivity(), OnClickListener, SignUpContract.View {

    private var presenter: SignUpContract.Presenter? = null
    private var user: User = User()
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.sign_up_screen)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        presenter = SignUpPresenter(this, UserRepository(UserRemoteDataSource(firebaseAuth, firebaseDatabase)))
        initComponents()
        requirePermissions()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            id.textBirthday -> showDatePicker()
            id.buttonSignUp -> handleSignUp()
        }
    }

    override fun onSignUpFailure() {
        this.notification(resources.getString(R.string.sign_up_existed))
        hideProgess()
    }

    override fun onSignUpSuccessfuly() {
        this.notification(resources.getString(R.string.sign_up_success))
        hideProgess()
    }

    override fun onEmptyUserName() {
        this.notification(resources.getString(R.string.sign_up_empty_user_name))
        hideProgess()
    }

    override fun onEmptyBirthday() {
        this.notification(resources.getString(R.string.sign_up_error_empty_birth_day))
        hideProgess()
    }

    override fun onEmptyGender() {
        this.notification(resources.getString(R.string.sign_up_empty_gender))
        hideProgess()
    }

    override fun onEmptyEmail() {
        this.notification(resources.getString(R.string.sign_up_empty_email))
        hideProgess()
    }

    override fun onEmptyPassword() {
        this.notification(resources.getString(R.string.sign_up_empty_password))
        hideProgess()
    }

    override fun onEmptyConfirmPassword() {
        this.notification(resources.getString(R.string.sign_up_empty_password))
        hideProgess()
    }

    override fun onLengthPasswordInvalid() {
        this.notification(resources.getString(R.string.sign_up_length_password_invalid))
        hideProgess()
    }

    override fun onCofirmPasswordInvalid() {
        this.notification(resources.getString(R.string.sign_up_confirm_password))
        hideProgess()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Granted
                } else {
                    requirePermissions()
                }
                return
            }
        }
    }

    private fun requirePermissions() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSION_CODE
            )
        } else {
            //Granted
        }
    }

    private fun initComponents() {
        textBirthday.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initProgressBar()
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun handleSignUp() {
        showProgress()
        val name = editFullname.text.toString()
        val birth = textBirthday.text.toString()
        val viewId = radioGroups.findViewById<RadioButton>(radioGroups.checkedRadioButtonId)
        val gender = radioGroups.indexOfChild(viewId).toString()
        email = editEmail.text.toString()
        password = editPassword.text.toString()
        confirmPassword = editConfirmPassword.text.toString()
        user = User(userName = name, birthday = birth, gender = gender)
        presenter?.signUp(user, email, password, confirmPassword)
    }

    private fun initProgressBar() {
        val bounds = progress.indeterminateDrawable.bounds
        val progressDrawable = ChromeFloatingCirclesDrawable.Builder(this)
            .colors(
                intArrayOf(
                    ContextCompat.getColor(this, color.red),
                    ContextCompat.getColor(this, color.blue),
                    ContextCompat.getColor(this, color.yellow),
                    ContextCompat.getColor(this, color.green)
                )
            )
            .build()
        progress.indeterminateDrawable = progressDrawable
        progress.indeterminateDrawable.bounds = bounds
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val callback =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                cal.set(year, month, day)
                val date = DateUtils.formatDateTime(this, cal.timeInMillis, DateUtils.FORMAT_SHOW_YEAR)
                textBirthday.setText(date.format(cal.time))
            }
        val picker = DatePickerDialog(
            this, callback, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.show()
    }

    private fun hideProgess() {
        progress.visibility = View.GONE
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    private fun Context.notification(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    companion object {
        val REQUEST_PERMISSION_CODE = 100
    }
}