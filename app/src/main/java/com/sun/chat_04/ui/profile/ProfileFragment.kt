package com.sun.chat_04.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
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
import com.sun.chat_04.ui.discovery.SpacesItemDecoration
import com.sun.chat_04.ui.login.LoginActivity
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_profile.imageAvatarProfile
import kotlinx.android.synthetic.main.fragment_profile.imageCover
import kotlinx.android.synthetic.main.fragment_profile.imageEditAvatar
import kotlinx.android.synthetic.main.fragment_profile.imageEditCover
import kotlinx.android.synthetic.main.fragment_profile.imageEditProfile
import kotlinx.android.synthetic.main.fragment_profile.imageGender
import kotlinx.android.synthetic.main.fragment_profile.progressBarUpdateUserAvatar
import kotlinx.android.synthetic.main.fragment_profile.progressBarUpdateUserCover
import kotlinx.android.synthetic.main.fragment_profile.progressLoadImages
import kotlinx.android.synthetic.main.fragment_profile.rcImages
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.fragment_profile.toolbarUserProfile
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class ProfileFragment : Fragment(), ProfileContract.View, OnClickListener {

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var user: User
    private lateinit var imageAdapter: ImageAdapter

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
    }

    override fun onFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.no_internet))
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

    override fun onGetUserImagesSuccess(images: List<String>?) {
        if (images.isNullOrEmpty()) return
        displayImages(images)
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
                    Constants.REQUEST_CODE_EDIT_INFO -> {
                        getUserProfile()
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
            R.id.imageAvatarProfile -> displayImageDetail(user.pathAvatar)
            R.id.imageCover -> displayImageDetail(user.pathBackground)
        }
    }

    override fun showLoadingImages() {
        progressLoadImages?.apply {
            visibility = View.VISIBLE
        }
    }

    override fun hideLoadingImage() {
        progressLoadImages?.apply {
            visibility = View.GONE
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

    private fun initComponent() {
        showIconSignOut()
        hideIconBackToolbar()
        imageEditAvatar.setOnClickListener(this)
        imageEditCover.setOnClickListener(this)
        imageEditProfile.setOnClickListener(this)
        imageAvatarProfile.setOnClickListener(this)
        imageCover.setOnClickListener(this)
        toolbarUserProfile.findViewById<ImageView>(R.id.imageSignOut)?.setOnClickListener(this)
    }

    private fun getUserProfile() {
        if (::presenter.isInitialized) {
            presenter.getUserProfile()
            presenter.getUserImages()
        }
    }

    private fun displayUserProfile() {
        if (::user.isInitialized) {
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
            displayUserGender()
            displayUserAddress()
        }
    }

    private fun displayUserGender() {
        textGenderProfile?.let {
            when (user.gender) {
                Constants.MALE -> {
                    textGenderProfile.text = resources.getString(R.string.male)
                    imageGender.setImageResource(R.drawable.ic_male)
                }
                else -> {
                    textGenderProfile.text = resources.getString(R.string.female)
                    imageGender.setImageResource(R.drawable.ic_female)
                }
            }
        }
    }

    private fun displayUserAddress() {
        context?.let {
            if (user.lat != 0.0 && user.lgn != 0.0) {
                val addressList = Geocoder(it, Locale.getDefault())
                    .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0].getAddressLine(0)
                    textAddressProfile.text = address ?: ""
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

    private fun displayImageDetail(uri: String?) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction ?: return
        with(transaction) {
            setCustomAnimations(R.anim.zoom_in, R.anim.translate_anim, R.anim.zoom_in, R.anim.translate_anim)
            replace(R.id.parentLayout, ImageDetailFragment.newInstance(uri))
            addToBackStack("")
            commit()
        }
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
        val editProfileFragment = EditProfileFragment.newInstance(user)
        editProfileFragment.setTargetFragment(this, Constants.REQUEST_CODE_EDIT_INFO)
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.parentLayout, editProfileFragment)
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

    private fun displayImages(images: List<String>) {
        imageAdapter = ImageAdapter(images as ArrayList<String>) { uri -> imageOnClick(uri) }
        with(rcImages) {
            layoutManager = GridLayoutManager(context, Constants.COLUMN)
            if (this.itemDecorationCount == 0) {
                addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_4)))
            }
            adapter = imageAdapter
        }
    }

    private fun imageOnClick(uri: String) {
        displayImageDetail(uri)
    }
}
