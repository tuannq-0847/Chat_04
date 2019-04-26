package com.sun.chat_04.ui.friend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.remote.FriendRemoteDataSource
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.chat.ChatFragment
import com.sun.chat_04.ui.friend.search.SearchFragment
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_friends.editSearch
import kotlinx.android.synthetic.main.fragment_friends.group
import kotlinx.android.synthetic.main.fragment_friends.imageSearch
import kotlinx.android.synthetic.main.fragment_friends.progressLoadFriend
import kotlinx.android.synthetic.main.fragment_friends.recyclerListChat

class FriendFragment : Fragment(), FriendContract.View, View.OnClickListener {
    private lateinit var adapter: FriendAdapter
    private lateinit var presenter: FriendContract.Presenter
    private lateinit var friendCallBack: FriendCallBack

    override fun onGetFriendsSuccessfully(friends: ArrayList<Friend>) {
        progressLoadFriend?.let {
            group.visibility = View.GONE
            recyclerListChat.visibility = View.VISIBLE
            progressLoadFriend.visibility = View.INVISIBLE
            adapter = FriendAdapter(friends) { friend -> onFriendSelectedListener(friend) }
            recyclerListChat.layoutManager = LinearLayoutManager(context)
            if (::adapter.isInitialized) {
                recyclerListChat.adapter = adapter
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editSearch.setOnClickListener(this)
        presenter = FriendPresenter(
            this, FriendRepository(
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
            presenter.getFriends()
        }
        editSearch.setOnClickListener(this)
        imageSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editSearch -> {
                val searchFragment = SearchFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.parentLayout, searchFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
            R.id.imageSearch -> {
                friendCallBack.openSearchScreen()
            }
        }
    }

    fun setOnFriendCallBack(friendCallBack: FriendCallBack) {
        this.friendCallBack = friendCallBack
    }

    override fun showEmptyData() {
        group.visibility = View.VISIBLE
        recyclerListChat.visibility = View.GONE
        progressLoadFriend.visibility = View.INVISIBLE
    }

    override fun onGetFriendsFailed(exception: Exception?) {
        progressLoadFriend.visibility = View.INVISIBLE
    }

    private fun onFriendSelectedListener(friend: Friend) {
        val chatFragment = ChatFragment.newInstance(friend)
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.parentLayout, chatFragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }
}
