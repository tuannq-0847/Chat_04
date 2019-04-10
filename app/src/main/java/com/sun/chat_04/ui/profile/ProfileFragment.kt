package com.sun.chat_04.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_profile.imageAvatarProfile
import kotlinx.android.synthetic.main.fragment_profile.imageCover
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshUserProfile
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class ProfileFragment : Fragment(), ProfileContract.View, OnClickListener {

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

    private fun initComponent() {
        imageAvatarProfile.setOnClickListener(this)
        imageCover.setOnClickListener(this)
    }

    override fun onGetUserProfileSuccess(user: User) {
        this.user = user
        displayUserProfile()
    }

    override fun onFailure(exception: Exception?) {
        hideSwipeRefreshUserProfile()
        Global.showMessage(context, resources.getString(R.string.no_internet))
    }

    override fun onUpdateUserAvatarSuccess(uri: Uri) {
        displayUserImage(uri, imageAvatarProfile)
        Global.showMessage(context, resources.getString(R.string.update_avatar_success))
    }

    override fun onUpdateUserCoverSuccess(uri: Uri) {
        displayUserImage(uri, imageCover)
        Global.showMessage(context, resources.getString(R.string.update_cover_success))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        resultIntent?.data?.let { uri ->
            if (resultCode == RESULT_OK) {
                when (requestCode) {
                    Constants.REQUEST_CODE_AVATAR -> ::presenter.isInitialized.let { presenter.updateUserAvatar(uri) }
                    Constants.REQUEST_CODE_COVER -> ::presenter.isInitialized.let { presenter.updateUserCover(uri) }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageAvatarProfile -> handleUpdateUserAvatar()
            R.id.imageCover -> handleUpdateUserCover()
        }
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

    private fun getUserProfile() {
        ::presenter.isInitialized.let { presenter.getUserProfile() }
    }

    private fun displayUserProfile() {
        ::user.isInitialized.let {
            displayUserImage(Uri.parse(user.pathAvatar), imageAvatarProfile)
            displayUserImage(Uri.parse(user.pathBackground), imageCover)
            textNameProfile.text = user.userName.toString()
            textAgeProfile.text = user.birthday.toString()
            textUserBioProfile.text = user.bio
            textNameToolbarProfile.text = user.userName.toString()
            textGenderProfile.text = when (user.gender) {
                Constants.MALE -> resources.getString(R.string.male)
                else -> resources.getString(R.string.female)
            }
            context?.let {
                Geocoder(it, Locale.getDefault())
                    .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)
                    .get(0)
                    .locality.let {
                    textAddressProfile.text = it
                }
            }
            hideSwipeRefreshUserProfile()
        }
    }

    private fun displayUserImage(uri: Uri, imageView: ImageView) {
        Glide.with(imageView)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.image_avatar)
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

    private fun hideSwipeRefreshUserProfile() {
        swipeRefreshUserProfile.isRefreshing = false
    }
}
