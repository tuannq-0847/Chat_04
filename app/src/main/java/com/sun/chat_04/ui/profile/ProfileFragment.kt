package com.sun.chat_04.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_profile.imageEditAvatar
import kotlinx.android.synthetic.main.fragment_profile.imageEditCover
import kotlinx.android.synthetic.main.fragment_profile.swipeRefreshUserProfile
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class ProfileFragment : Fragment(), ProfileContract.View {

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
        getUserProfile()
        handleUpdateUserImage(imageEditAvatar, Constants.RESULT_CODE_AVATAR)
        handleUpdateUserImage(imageEditCover, Constants.RESULT_CODE_COVER)
    }

    override fun onGetUserProfileSuccess(user: User) {
        this.user = user
        displayUserProfile()
    }

    override fun onFailure(exception: Exception?) {
        hideSwipeRefreshUserProfile()
        Global.showMessage(context, resources.getString(R.string.no_internet))
    }

    override fun onUpdateUserImageSuccess(uri: Uri, field: String) {
        var imageView: ImageView? = null
        when (field) {
            Constants.PATH_AVATAR -> imageView = imageAvatarProfile
            Constants.PATH_COVER -> imageView = imageCover
        }
        imageView?.let {
            displayUserImage(uri, it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        resultIntent?.data?.let { uri ->
            if (resultCode == RESULT_OK) {
                var field = ""
                when (requestCode) {
                    Constants.RESULT_CODE_AVATAR -> field = Constants.PATH_AVATAR
                    Constants.RESULT_CODE_COVER -> field = Constants.PATH_COVER
                }
                if (!field.isEmpty())
                    ::presenter.isInitialized.let {
                        presenter.updateUserImage(uri, field)
                    }
            }
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
        if (!::user.isInitialized) return
        displayUserImage(Uri.parse(user.pathAvatar), imageAvatarProfile)
        displayUserImage(Uri.parse(user.pathBackground), imageCover)
        textNameProfile.text = user.userName.toString()
        textAgeProfile.text = user.birthday.toString()
        textUserBioProfile.text = user.bio
        textNameToolbarProfile.text = user.userName.toString()
        when {
            user.gender.equals(Constants.MALE) -> textGenderProfile.text = resources.getString(R.string.male)
            user.gender.equals(Constants.FEMALE) -> textGenderProfile.text = resources.getString(R.string.female)
        }
        activity?.let {
            Geocoder(it, Locale.getDefault())
                .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)
                .get(0)
                .locality.let {
                textAddressProfile.text = it
            }
        }
        hideSwipeRefreshUserProfile()
    }

    private fun displayUserImage(uri: Uri, imageView: ImageView) {
        Glide.with(imageView)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.image_avatar)
            .into(imageView)
    }

    private fun handleUpdateUserImage(imageView: ImageView, resultCode: Int) {
        imageView.setOnClickListener {
            val intent = Intent()
            intent.type = Constants.INTENT_GALLERY
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, Constants.TITLE_GALLERY), resultCode)
        }
    }

    private fun hideSwipeRefreshUserProfile() {
        swipeRefreshUserProfile.isRefreshing = false
    }
}
