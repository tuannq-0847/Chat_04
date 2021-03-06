package com.sun.chat_04.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.login.LoginActivity
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_profile.imageAvatarProfile
import kotlinx.android.synthetic.main.fragment_profile.imageCover
import kotlinx.android.synthetic.main.fragment_profile.imageEditAvatar
import kotlinx.android.synthetic.main.fragment_profile.imageEditCover
import kotlinx.android.synthetic.main.fragment_profile.imageEditProfile
import kotlinx.android.synthetic.main.fragment_profile.progressBarUpdateUserAvatar
import kotlinx.android.synthetic.main.fragment_profile.progressBarUpdateUserCover
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshUserProfile
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.fragment_profile.toolbarUserProfile
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class ProfileFragment : Fragment(), ProfileContract.View, OnClickListener, OnRefreshListener {
    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initComponent()
        getUserProfile()
    }

    override fun onGetUserProfileSuccess(user: User) {
        this.user = user
        displayUserProfile()
        hideSwipeRefreshUserProfile()
    }

    override fun onFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.no_internet))
        hideSwipeRefreshUserProfile()
    }

    override fun onUpdateUserAvatarSuccess(uri: Uri) {
        displayUserImage(uri, imageAvatarProfile)
        Global.showMessage(context, resources.getString(R.string.update_avatar_success))
        hideProgressUpdateUserAvatar()
    }

    override fun onUpdateUserCoverSuccess(uri: Uri) {
        displayUserImage(uri, imageCover)
        Global.showMessage(context, resources.getString(R.string.update_cover_success))
        hideProgressUpdateUserCover()
    }

    override fun onSignOutSuccessfully() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        resultIntent?.data?.let { uri ->
            if (resultCode == RESULT_OK) {
                when (requestCode) {
                    Constants.REQUEST_CODE_AVATAR -> {
                        if (::presenter.isInitialized) {
                            presenter.updateUserAvatar(uri)
                            showProgressUpdateUserAvatar()
                        }
                    }
                    Constants.REQUEST_CODE_COVER -> {
                        if (::presenter.isInitialized) {
                            presenter.updateUserCover(uri)
                            showProgressUpdateUserCover()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageEditAvatar -> handleUpdateUserAvatar()
            R.id.imageEditCover -> handleUpdateUserCover()
            R.id.imageSignOut -> handleSignOut()
            R.id.imageEditProfile -> handleEditUserProfile()
        }
    }

    override fun onRefresh() {
        getUserProfile()
    }

    private fun initPresenter() {
        presenter = ProfilePresenter(
            this,
            UserRepository(
                UserRemoteDataSource(
                    Global.firebaseAuth,
                    Global.firebaseDatabase,
                    Global.firebaseStorage
                )
            )
        )
    }

    private fun initComponent() {
        showIconSignOut()
        hideIconBackToolbar()
        imageEditAvatar.setOnClickListener(this)
        imageEditCover.setOnClickListener(this)
        imageEditProfile.setOnClickListener(this)
        toolbarUserProfile.findViewById<ImageView>(R.id.imageSignOut)?.setOnClickListener(this)
        swipeRefreshUserProfile.setOnRefreshListener(this)
    }

    private fun getUserProfile() {
        if (::presenter.isInitialized) {
            presenter.getUserProfile()
        }
    }

    private fun displayUserProfile() {
        ::user.isInitialized.let {
            imageAvatarProfile?.let {
                displayUserImage(Uri.parse(user.pathAvatar), it)
            }
            imageCover?.let {
                displayUserImage(Uri.parse(user.pathBackground), it)
            }
            textNameProfile?.let {
                it.text = user.userName.toString()
            }
            textAgeProfile?.let {
                it.text = user.birthday.toString()
            }
            textUserBioProfile?.let {
                it.text = user.bio
            }
            textNameToolbarProfile?.let {
                it.text = user.userName.toString()
            }
            textGenderProfile?.let {
                it.text = when (user.gender) {
                    Constants.MALE -> resources.getString(R.string.male)
                    else -> resources.getString(R.string.female)
                }
            }
            context?.let {
                if (user.lat != 0.0 && user.lgn != 0.0) {
                    Geocoder(it, Locale.getDefault())
                        .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)[0]
                        .locality.let {
                        textAddressProfile.text = it
                    }
                }
            }
        }
    }

    private fun displayUserImage(uri: Uri, imageView: ImageView) {
        var resourceId = R.drawable.gradient_header_background
        if (imageView == imageAvatarProfile) resourceId = R.drawable.avatar
        Glide.with(imageView)
            .load(uri)
            .centerCrop()
            .placeholder(resourceId)
            .into(imageView)
    }

    private fun handleUpdateUserAvatar() {
        val intent = Intent()
        intent.type = Constants.INTENT_GALLERY
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, resources.getString(R.string.select_image)),
            Constants.REQUEST_CODE_AVATAR
        )
    }

    private fun handleUpdateUserCover() {
        val intent = Intent()
        intent.type = Constants.INTENT_GALLERY
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, resources.getString(R.string.select_image)),
            Constants.REQUEST_CODE_COVER
        )
    }

    private fun handleEditUserProfile() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.parentLayout, EditProfileFragment.newInstance(user))
            ?.addToBackStack("")
            ?.commit()
    }

    private fun handleSignOut() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(resources.getString(R.string.sign_out))
                .setMessage(resources.getString(R.string.sign_out_notify))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    if (::presenter.isInitialized)
                        presenter.updateUserStatus(Constants.OFFLINE)
                    Global.firebaseAuth.signOut()
                    LoginManager.getInstance().logOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNeutralButton(resources.getString(R.string.no)) { dialog, which -> }
                .show()
        }
    }

    private fun hideSwipeRefreshUserProfile() {
        swipeRefreshUserProfile?.let {
            swipeRefreshUserProfile.isRefreshing = false
        }
    }

    private fun hideProgressUpdateUserAvatar() {
        progressBarUpdateUserAvatar?.let {
            progressBarUpdateUserAvatar.visibility = View.GONE
        }
    }

    private fun showProgressUpdateUserAvatar() {
        progressBarUpdateUserAvatar?.let {
            progressBarUpdateUserAvatar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressUpdateUserCover() {
        progressBarUpdateUserCover?.let {
            progressBarUpdateUserCover.visibility = View.GONE
        }
    }

    private fun showProgressUpdateUserCover() {
        progressBarUpdateUserCover?.let {
            progressBarUpdateUserCover.visibility = View.VISIBLE
        }
    }

    private fun hideIconBackToolbar() {
        toolbarUserProfile?.let {
            toolbarUserProfile.findViewById<ImageView>(R.id.imageBackProfile).visibility = View.INVISIBLE
        }
    }

    private fun showIconSignOut() {
        toolbarUserProfile.findViewById<ImageView>(R.id.imageSignOut)?.let {
            it.findViewById<ImageView>(R.id.imageSignOut).visibility = View.VISIBLE
        }
    }
}
