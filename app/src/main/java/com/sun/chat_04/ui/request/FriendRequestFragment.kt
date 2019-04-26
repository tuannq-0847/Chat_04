package com.sun.chat_04.ui.request

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.FriendRemoteDataSource
import com.sun.chat_04.data.remote.FriendRequestRemoteDataSource
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.data.repositories.FriendRequestRepository
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.chat.ChatFragment
import com.sun.chat_04.ui.friend.FriendCallBack
import com.sun.chat_04.ui.frienddetail.FriendDetailFragment
import com.sun.chat_04.ui.request.FriendsAdapter.OnDeleteFriends
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_friend_request.imageEmpty
import kotlinx.android.synthetic.main.fragment_friend_request.recyclerFriendRequest
import kotlinx.android.synthetic.main.fragment_friend_request.recyclerFriends
import kotlinx.android.synthetic.main.fragment_friend_request.textEmptyRequest

class FriendRequestFragment : Fragment(), FriendRequestContract.View, OnDeleteFriends {
    private lateinit var presenter: FriendRequestContract.Presenter
    private lateinit var adapter: FriendRequestAdapter
    private lateinit var friendCallBack: FriendCallBack
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onFriendRequestsAvailable(friendRequests: ArrayList<User>) {
        if (friendRequests.size == FRIENDS_INDEX_0) {
            textEmptyRequest?.let {
                it.visibility = View.VISIBLE
                recyclerFriendRequest.visibility = View.GONE
            }
        } else {
            textEmptyRequest?.let {
                it.visibility = View.GONE
                recyclerFriendRequest.visibility = View.VISIBLE
                adapter =
                    FriendRequestAdapter(friendRequests) { user, buttonId -> onClickListener(user, buttonId) }
                if (::adapter.isInitialized) {
                    recyclerFriendRequest.layoutManager = LinearLayoutManager(context)
                    recyclerFriendRequest.adapter = adapter
                    adapter.refreshFriendRequest()
                }
            }
        }
    }

    override fun onGetFriendsSuccesfully(friends: ArrayList<Friend>) {
        recyclerFriends?.let {
            recyclerFriends.visibility = View.VISIBLE
            imageEmpty.visibility = View.GONE
            if (::presenter.isInitialized) {
                friendsAdapter = FriendsAdapter(presenter.sortByName(friends))
                if (::friendsAdapter.isInitialized) {
                    recyclerFriends.layoutManager = LinearLayoutManager(context)
                    recyclerFriends.adapter = friendsAdapter
                    friendsAdapter.setOnDeleteListener(this)
                }
            }
        }
    }

    override fun onFailure(exception: Exception?) {
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun onClickListener(user: User, buttonId: Int) {
        if (::presenter.isInitialized) {
            when (buttonId) {
                R.id.buttonAccept -> {
                    presenter.approveFriendRequest(user)
                }
                R.id.buttonCancel -> {
                    presenter.cancelFriendRequest(user)
                }
            }
        }
    }

    fun setOnFriendRequestCallBack(friendCallBack: FriendCallBack) {
        this.friendCallBack = friendCallBack
    }

    override fun onDeleteFriendSuccessfully(userName: String) {
        Toast.makeText(
            context,
            resources.getString(R.string.unfriend) + " $userName " + resources.getString(R.string.successfully)
            ,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onApproveSuccessfully(userName: String?) {
        Toast.makeText(context, resources.getString(R.string.notice_friend) + " $userName", Toast.LENGTH_SHORT).show()
        if (::friendCallBack.isInitialized) {
            friendCallBack.openFriendScreen()
        }
    }

    override fun onItemDeleteClicked(friend: Friend) {
        if (::presenter.isInitialized) {
            presenter.deleteFriend(friend)
        }
    }

    override fun showEmptyFriends() {
        recyclerFriends?.let {
            recyclerFriends.visibility = View.GONE
            imageEmpty.visibility = View.VISIBLE
        }
    }

    override fun onItemViewClicked(friend: Friend) {
        if (::presenter.isInitialized) {
            presenter.getUserFromFriend(friend)
        }
    }

    override fun onGetUserSuccessfully(user: User) {
        val peopleInformation = FriendDetailFragment.newInstance(user)
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.parentLayout, peopleInformation)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onCancelSuccessfully() {
    }

    override fun onItemChatClicked(friend: Friend) {
        val chatFragment = ChatFragment.newInstance(friend)
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.parentLayout, chatFragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friend_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = FriendRequestPresenter(
            this, FriendRequestRepository(
                FriendRequestRemoteDataSource(
                    Global.firebaseAuth, Global.firebaseDatabase
                )
            ),
            FriendRepository(
                FriendRemoteDataSource(
                    Global.firebaseDatabase
                )
            ),
            UserRepository(
                UserRemoteDataSource(
                    Global.firebaseAuth,
                    Global.firebaseDatabase,
                    Global.firebaseStorage
                )
            )
        )
        if (::presenter.isInitialized) {
            presenter.getFriendRequests()
            presenter.getFriends()
        }
    }

    companion object {
        const val FRIENDS_INDEX_0 = 0
    }
}
