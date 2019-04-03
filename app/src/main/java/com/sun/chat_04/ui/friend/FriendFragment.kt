package com.sun.chat_04.ui.friend

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
import com.sun.chat_04.data.remote.FriendRemoteDataSource
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.ui.friend.search.SearchFragment
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_friends.editSearch
import kotlinx.android.synthetic.main.fragment_friends.progressLoadFriend
import kotlinx.android.synthetic.main.fragment_friends.recyclerListChat

class FriendFragment : Fragment(), FriendContract.View, View.OnClickListener {
    private lateinit var adapter: FriendAdapter
    private lateinit var presenter: FriendContract.Presenter

    override fun onGetFriendsSuccessfully(friends: ArrayList<Friend>) {
        progressLoadFriend.visibility = View.INVISIBLE
        adapter = FriendAdapter(friends) { lastMessage -> listener(lastMessage) }
        recyclerListChat.layoutManager = LinearLayoutManager(context)
        if (::adapter.isInitialized) {
            recyclerListChat.adapter = adapter
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
            )
        )
        if (::presenter.isInitialized) {
            presenter.getFriends()
        }
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
        }
    }

    override fun onGetFriendsFailed(exception: Exception?) {
        progressLoadFriend.visibility = View.INVISIBLE
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun listener(friend: Friend) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }
}
