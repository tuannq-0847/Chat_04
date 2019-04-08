package com.sun.chat_04.ui.friend.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.remote.FriendRemoteDataSource
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_search_chat.imageBack
import kotlinx.android.synthetic.main.fragment_search_chat.recyclerSearch
import kotlinx.android.synthetic.main.fragment_search_chat.searchMessage

class SearchFragment : Fragment(), SearchConstract.View, View.OnClickListener, OnQueryTextListener {
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var presenter: SearchConstract.Presenter

    override fun onGetUsersFailure(exception: Exception?) {
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onGetUsersSuccessfully(friends: ArrayList<Friend>) {
        searchAdapter = SearchAdapter(friends)
        recyclerSearch.layoutManager = LinearLayoutManager(context)
        if (::searchAdapter.isInitialized) {
            recyclerSearch.adapter = searchAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SearchPresenter(
            this,
            FriendRepository(
                FriendRemoteDataSource(
                    Global.firebaseDatabase
                )
            )
        )

        searchMessage.onActionViewExpanded()
        searchMessage.setOnQueryTextListener(this)
        imageBack.setOnClickListener(this)
    }

    override fun onQueryTextSubmit(text: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(text: String?): Boolean {
        text?.let {
            if (::presenter.isInitialized) {
                presenter.searchUser(text)
            }

        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageBack -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_chat, container, false)
    }
}
