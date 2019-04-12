package com.sun.chat_04.ui.frienddetail

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
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class FriendDetailFragment : Fragment(), FriendDetailContract.View, OnClickListener {

    private lateinit var presenter: FriendDetailContract.Presenter
    private val user: User by lazy { arguments?.getParcelable(Constants.ARGUMENT_FRIENDS) as User }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_information, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        displayUserProfile()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonAddFriend -> ::presenter.isInitialized.let { presenter.inviteMoreFriends(user.idUser) }
            R.id.buttonBack -> handleBackPrevios()
            R.id.imageBackProfile -> handleBackPrevios()
        }
    }

    override fun showButtonChat() {
    }

    override fun showButtonInviteMoreFriends() {
    }

    override fun showButtonCancelInviteMoreFriends() {
    }

    override fun onFailure(exception: Exception?) {
        // fail
    }

    private fun initPresenter() {
        presenter = FriendDetailPresenter(
            this,
            UserRepository(UserRemoteDataSource(Global.firebaseAuth, Global.firebaseDatabase, Global.firebaseStorage))
        )
    }

    private fun displayUserProfile() {
        displayUserImage(Uri.parse(user.pathAvatar), imageAvatarProfile)
        displayUserImage(Uri.parse(user.pathBackground), imageCover)
        textNameProfile.text = user.userName.toString()
        textAgeProfile.text = user.birthday
        textUserBioProfile.text = user.bio
        textNameToolbarProfile.text = user.userName
        textGenderProfile.text = when (user.gender) {
            Constants.MALE -> resources.getString(R.string.male)
            else -> resources.getString(R.string.female)
        }
        context?.let {
            Geocoder(it, Locale.getDefault())
                .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)
                .get(0).locality.let {
                textAddressProfile.text = it
            }
        }
    }

    private fun displayUserImage(uri: Uri, imageView: ImageView) {
        Glide.with(imageView)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.image_avatar)
            .into(imageView)
    }

    private fun handleBackPrevios() {
        // Back previos
    }

    companion object {
        @JvmStatic
        fun newInstance(user: User) = FriendDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.ARGUMENT_FRIENDS, user)
            }
        }
    }
}
