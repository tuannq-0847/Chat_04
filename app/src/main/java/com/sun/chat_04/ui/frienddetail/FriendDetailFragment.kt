package com.sun.chat_04.ui.frienddetail

import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
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
import com.sun.chat_04.ui.discovery.SpacesItemDecoration
import com.sun.chat_04.ui.profile.ImageAdapter
import com.sun.chat_04.ui.profile.ImageDetailFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_user_information.floatingAddFriend
import kotlinx.android.synthetic.main.fragment_user_information.imageAvatarProfile
import kotlinx.android.synthetic.main.fragment_user_information.imageCover
import kotlinx.android.synthetic.main.fragment_user_information.imageGender
import kotlinx.android.synthetic.main.fragment_user_information.progressLoadImages
import kotlinx.android.synthetic.main.fragment_user_information.rcImages
import kotlinx.android.synthetic.main.fragment_user_information.textAddressProfile
import kotlinx.android.synthetic.main.fragment_user_information.textAgeProfile
import kotlinx.android.synthetic.main.fragment_user_information.textGenderProfile
import kotlinx.android.synthetic.main.fragment_user_information.textNameProfile
import kotlinx.android.synthetic.main.fragment_user_information.textUserBioProfile
import kotlinx.android.synthetic.main.fragment_user_information.toolbarFriendDetail
import kotlinx.android.synthetic.main.toolbar_profile.textNameToolbarProfile
import java.util.Locale

class FriendDetailFragment : Fragment(), FriendDetailContract.View, OnClickListener {

    private lateinit var presenter: FriendDetailContract.Presenter
    private lateinit var adapter: ImageAdapter
    private val user: User by lazy { arguments?.getParcelable(Constants.ARGUMENT_FRIENDS) as User }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onActivityCreated(savedInstanceState)
        initComponent()
        getFriendImages()
        displayUserProfile()
        checkIsFriend()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatingAddFriend -> handleFloatingClick()
            R.id.imageBackProfile -> handleBackPrevious()
            R.id.imageAvatarProfile -> displayImageDetail(user.pathAvatar)
            R.id.imageCover -> displayImageDetail(user.pathBackground)
        }
    }

    override fun showButtonChat() {
        context?.let { context ->
            floatingAddFriend?.let {
                it.supportBackgroundTintList = ContextCompat.getColorStateList(context, R.color.color_blue)
                it.setImageResource(R.drawable.ic_chat)
                it.tag = resources.getString(R.string.button_chat)
            }
        }
    }

    override fun showButtonInviteMoreFriends() {
        context?.let { context ->
            floatingAddFriend?.let {
                it.supportBackgroundTintList = ContextCompat.getColorStateList(context, R.color.color_green)
                it.setImageResource(R.drawable.ic_invite_more_friends)
                it.tag = resources.getString(R.string.invite_more_friends)
            }
        }
    }

    override fun showButtonCancelInviteMoreFriends() {
        context?.let { context ->
            floatingAddFriend?.let {
                it.supportBackgroundTintList = ContextCompat.getColorStateList(context, R.color.color_red)
                it.setImageResource(R.drawable.ic_cancel)
                it.tag = resources.getString(R.string.cancel_invite_more_friends)
            }
        }
    }

    override fun onGetFriendImagesSuccess(images: List<String>?) {
        if (images.isNullOrEmpty()) return
        displayFriendImages(images)
    }

    override fun onFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.no_internet))
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
        presenter = FriendDetailPresenter(
            this,
            UserRepository(UserRemoteDataSource(Global.firebaseAuth, Global.firebaseDatabase, Global.firebaseStorage))
        )
    }

    private fun initComponent() {
        imageAvatarProfile.setOnClickListener(this)
        imageCover.setOnClickListener(this)
        floatingAddFriend.setOnClickListener(this)
        toolbarFriendDetail.findViewById<ImageView>(R.id.imageBackProfile)?.setOnClickListener(this)
        hideIconSignOut()
    }

    private fun displayUserProfile() {
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
            if (user.lat != LAT_DEFAULT && user.lgn != LGN_DEFAULT) {
                val addressList = Geocoder(it, Locale.getDefault())
                    .getFromLocation(user.lat, user.lgn, Constants.MAX_ADDRESS)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0].getAddressLine(0)
                    textAddressProfile.text = address ?: ""
                }
            }
        }
    }

    private fun displayFriendImages(images: List<String>) {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.dp_4)
        adapter = ImageAdapter(images as ArrayList<String>) { uri -> imageOnClick(uri) }
        rcImages?.apply {
            layoutManager = GridLayoutManager(context, Constants.COLUMN)
            addItemDecoration(SpacesItemDecoration(spacingInPixels))
            adapter = adapter
        }
    }

    private fun imageOnClick(uri: String) {
        displayImageDetail(uri)
    }

    private fun displayImageDetail(uri: String?) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction ?: return
        with(transaction) {
            setCustomAnimations(R.anim.zoom_in, R.anim.translate_anim, R.anim.zoom_in, R.anim.translate_anim)
            add(R.id.parentLayout, ImageDetailFragment.newInstance(uri))
            addToBackStack("")
            commit()
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

    private fun getFriendImages() {
        if (::presenter.isInitialized) {
            presenter.getFriendImages()
        }
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
            val friend = Friend(user.idUser, user.pathAvatar, user.online, userName = it)
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

        const val LAT_DEFAULT = 0.0
        const val LGN_DEFAULT = 0.0
    }
}
