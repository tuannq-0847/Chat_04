package com.sun.chat_04.ui.request

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.FriendRequestRemoteDataSource
import com.sun.chat_04.data.repositories.FriendRequestRepository
import com.sun.chat_04.ui.friend.FriendCallBack
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_friend_request.imageEmpty
import kotlinx.android.synthetic.main.fragment_friend_request.recyclerFriendRequest

class FriendRequestFragment : Fragment(), FriendRequestContract.View {
    private lateinit var presenter: FriendRequestContract.Presenter
    private lateinit var adapter: FriendRequestAdapter
    private lateinit var friendCallBack: FriendCallBack

    override fun onFriendRequestsAvailable(friendRequests: ArrayList<User>) {
        if (friendRequests.size == FRIENDS_INDEX_0) {
            imageEmpty?.let {
                it.visibility = View.VISIBLE
                recyclerFriendRequest.visibility = View.GONE
            }
        } else {
            imageEmpty?.let {
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

    override fun onFailure(exception: Exception?) {
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun onClickListener(user: User, buttonId: Int) {
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

    override fun onApproveSuccessfully(userName: String?) {
        Toast.makeText(context, resources.getString(R.string.notice_friend) + " $userName", Toast.LENGTH_SHORT).show()
        if (::friendCallBack.isInitialized) {
            friendCallBack.openFriendScreen()
        }
    }

    override fun onCancelSuccessfully() {
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
            )
        )
        if (::presenter.isInitialized) {
            presenter.getFriendRequests()
        }
    }

    companion object {
        const val FRIENDS_INDEX_0 = 0
    }
}
