package com.sun.chat_04.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_edit_user_profile.editAgeProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.editBioEditUser
import kotlinx.android.synthetic.main.fragment_edit_user_profile.editNameProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.progessEditUserProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.radioFemaleEditUserProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.radioGroupEditUserProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.radioMaleEditUserProfile
import kotlinx.android.synthetic.main.fragment_edit_user_profile.toolbarEditUserProfile
import kotlinx.android.synthetic.main.toolbar_profile.imageSignOut
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile

class EditProfileFragment : Fragment(), EditProfileContract.View, OnClickListener, OnCheckedChangeListener {

    private val user by lazy { arguments?.get(Constants.ARGUMENT_USER) as User }
    private lateinit var presenter: EditProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user_profile, container, false)
        view.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageSignOut -> handleEditUserProfile()
            R.id.imageBackProfile -> handleBackPrevious()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        user.gender = group?.indexOfChild(group.findViewById(checkedId)).toString()
    }

    override fun onEmptyNewName() {
        hideProgress()
        Global.showMessage(context, resources.getString(R.string.sign_up_empty_user_name))
    }

    override fun onEmptyNewGender() {
        hideProgress()
        Global.showMessage(context, resources.getString(R.string.sign_up_empty_gender))
    }

    override fun onEmptyNewBirthday() {
        hideProgress()
        Global.showMessage(context, resources.getString(R.string.sign_up_error_empty_birth_day))
    }

    override fun onEditUserProfileSuccess() {
        hideProgress()
        Global.showMessage(context, resources.getString(R.string.edit_profile_success))
        fragmentManager?.popBackStack()
    }

    override fun onEditUserProfileFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.edit_profile_fail))
        hideProgress()
    }

    private fun initComponents() {
        editNameProfile.setText(user.userName.toString())
        editAgeProfile.setText(user.birthday.toString())
        editBioEditUser.setText(user.bio)
        when (user.gender.toString()) {
            Constants.MALE -> radioMaleEditUserProfile.isChecked = true
            Constants.FEMALE -> radioFemaleEditUserProfile.isChecked = true
        }
        textNameToolbarProfile.setText(resources.getString(R.string.title_modify_info_user))
        imageSignOut.setImageResource(R.drawable.ic_done)

        toolbarEditUserProfile.findViewById<ImageView>(R.id.imageSignOut).setOnClickListener(this)
        toolbarEditUserProfile.findViewById<ImageView>(R.id.imageBackProfile).setOnClickListener(this)
        radioGroupEditUserProfile.setOnCheckedChangeListener(this)
    }

    private fun initPresenter() {
        presenter = EditProfilePresenter(
            this,
            UserRepository(UserRemoteDataSource(Global.firebaseAuth, Global.firebaseDatabase, Global.firebaseStorage))
        )
    }

    private fun handleEditUserProfile() {
        showProgress()
        user.userName = editNameProfile.text.toString()
        user.birthday = editAgeProfile.text.toString()
        user.bio = editBioEditUser.text.toString()
        if (::presenter.isInitialized) {
            presenter.editUserProfile(user)
        }
        Intent().apply {
            data = Uri.parse("")
            targetFragment?.onActivityResult(Constants.REQUEST_CODE_EDIT_INFO, RESULT_OK, this)
        }
    }

    private fun handleBackPrevious() {
        fragmentManager?.popBackStack()
    }

    private fun showProgress() {
        progessEditUserProfile?.let {
            progessEditUserProfile.visibility = View.VISIBLE
        }
    }

    private fun hideProgress() {
        progessEditUserProfile?.let {
            progessEditUserProfile.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(user: User) = EditProfileFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.ARGUMENT_USER, user)
            }
        }
    }
}
