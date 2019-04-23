package com.sun.chat_04.ui.frienddetail

import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.chat.ChatFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_profile.imageAvatarProfile
import kotlinx.android.synthetic.main.fragment_profile.imageCover
import kotlinx.android.synthetic.main.fragment_profile.textAddressProfile
import kotlinx.android.synthetic.main.fragment_profile.textAgeProfile
import kotlinx.android.synthetic.main.fragment_profile.textGenderProfile
import kotlinx.android.synthetic.main.fragment_profile.textNameProfile
import kotlinx.android.synthetic.main.fragment_profile.textUserBioProfile
import kotlinx.android.synthetic.main.fragment_user_information.floatingAddFriend
import kotlinx.android.synthetic.main.fragment_user_information.toolbarFriendDetail
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
        initComponent()
        displayUserProfile()
        checkIsFriend()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatingAddFriend -> handleFloatingClick()
            R.id.imageBackProfile -> handleBackPrevious()
        }
    }

    override fun showButtonChat() {
        context?.let {
            floatingAddFriend.supportBackgroundTintList = ContextCompat.getColorStateList(it, R.color.color_blue)
            floatingAddFriend.setImageResource(R.drawable.ic_chat)
            floatingAddFriend.tag = resources.getString(R.string.button_chat)
        }
    }

    override fun showButtonInviteMoreFriends() {
        context?.let {
            floatingAddFriend.supportBackgroundTintList = ContextCompat.getColorStateList(it, R.color.color_green)
            floatingAddFriend.setImageResource(R.drawable.ic_invite_more_friends)
            floatingAddFriend.tag = resources.getString(R.string.invite_more_friends)
        }
    }

    override fun showButtonCancelInviteMoreFriends() {
        context?.let {
            floatingAddFriend.supportBackgroundTintList = ContextCompat.getColorStateList(it, R.color.color_red)
            floatingAddFriend.setImageResource(R.drawable.ic_cancel)
            floatingAddFriend.tag = resources.getString(R.string.cancel_invite_more_friends)
        }
    }

    override fun onFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.no_internet))
    }

    private fun initPresenter() {
        presenter = FriendDetailPresenter(
            this,
            UserRepository(UserRemoteDataSource(Global.firebaseAuth, Global.firebaseDatabase, Global.firebaseStorage))
        )
    }

    private fun initComponent() {
        floatingAddFriend.setOnClickListener(this)
        toolbarFriendDetail.findViewById<ImageView>(R.id.imageBackProfile)?.setOnClickListener(this)
        hideIconSignOut()
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
                .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)[0].locality.let { it1 ->
                textAddressProfile.text = it1
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

    private fun checkIsFriend() {
        if (::presenter.isInitialized) {
            presenter.checkIsFriend(user.idUser)
        }
    }

    private fun handleFloatingClick() {
        when (floatingAddFriend.tag) {
            resources.getString(R.string.button_chat) -> handleChat()
            resources.getString(R.string.invite_more_friends) -> handleInviteMoreFriends()
            resources.getString(R.string.cancel_invite_more_friends) -> handleCancelInviteMoreFriends()
        }
    }

    private fun handleBackPrevious() {
        fragmentManager?.popBackStack()
    }

    private fun handleChat() {
        user.userName.let {
            val friend = Friend(user.idUser, user.pathAvatar, user.isOnline, userName = it)
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.parentLayout, ChatFragment.newInstance(friend))
                ?.addToBackStack("")
                ?.commit()
        }
    }

    private fun handleInviteMoreFriends() {
        if (::presenter.isInitialized) {
            presenter.inviteMoreFriends(user.idUser)
        }
    }

    private fun handleCancelInviteMoreFriends() {
        if (::presenter.isInitialized) {
            presenter.cancelInviteMoreFriends(user.idUser)
        }
    }

    private fun hideIconSignOut() {
        toolbarFriendDetail.findViewById<ImageView>(R.id.imageSignOut)?.let {
            it.visibility = INVISIBLE
        }
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
