package com.sun.chat_04.ui.signup

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.DatePickerDialog
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
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.sun.chat_04.R.color
import com.sun.chat_04.R.id
import com.sun.chat_04.R.layout
import com.sun.chat_04.data.model.User
import kotlinx.android.synthetic.main.sign_up_screen.btnSignUp
import kotlinx.android.synthetic.main.sign_up_screen.edtConfirmPassword
import kotlinx.android.synthetic.main.sign_up_screen.edtEmail
import kotlinx.android.synthetic.main.sign_up_screen.edtFullName
import kotlinx.android.synthetic.main.sign_up_screen.edtPassword
import kotlinx.android.synthetic.main.sign_up_screen.progress
import kotlinx.android.synthetic.main.sign_up_screen.radio_groups
import kotlinx.android.synthetic.main.sign_up_screen.toolbar
import kotlinx.android.synthetic.main.sign_up_screen.txtBirthday
import java.util.Calendar

class SignUpActivity : AppCompatActivity(), OnClickListener {
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.sign_up_screen)
        initComponents()
        requirePermissions()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            id.txtBirthday -> showDatePicker()
            id.btnSignUp -> handleSignUp()
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
        txtBirthday.setOnClickListener(this)
        btnSignUp.setOnClickListener(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun handleSignUp() {
        displayProgressBar()
        progress.visibility = View.VISIBLE
        val name = edtFullName.text.toString()
        val birth = txtBirthday.text.toString()
        val viewId = radio_groups.findViewById<RadioButton>(radio_groups.checkedRadioButtonId)
        val gender = radio_groups.indexOfChild(viewId).toString()
        val email = edtEmail.text.toString()
        val pass = edtPassword.text.toString()
        val confirmPass = edtConfirmPassword.text.toString()
        user = User(name, birth, gender)
    }

    private fun displayProgressBar() {
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
                txtBirthday.setText(date.format(cal.time))
            }
        val picker = DatePickerDialog(
            this, callback, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.show()
    }

    companion object {
        val REQUEST_PERMISSION_CODE = 100
    }
}
