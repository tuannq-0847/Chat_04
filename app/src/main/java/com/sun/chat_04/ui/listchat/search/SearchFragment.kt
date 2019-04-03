package com.sun.chat_04.ui.listchat.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.data.remote.LastMessageRemoteDataSource
import com.sun.chat_04.data.repositories.LastMessageRepository
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.fragment_search_chat.recyclerSearch
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_search_chat.searchMessage

class SearchFragment : Fragment(), SearchConstract.View {

    private lateinit var mSearchAdapter: SearchAdapter
    private lateinit var presenter: SearchConstract.Presenter

    override fun onGetListUserFailure(exception: Exception?) {
        Toast.makeText(context, exception?.message.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onGetListUserSuccesfully(lastMessages: ArrayList<LastMessage>) {
        Log.d("TAG", lastMessages.size.toString())
        mSearchAdapter = SearchAdapter(lastMessages)
        recyclerSearch.layoutManager = LinearLayoutManager(context)
        recyclerSearch.adapter = mSearchAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SearchPresenter(
            this,
            LastMessageRepository(
                LastMessageRemoteDataSource(
                    Global.firebaseAuth, Global.firebaseDatabase
                )
            )
        )

        searchMessage.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    presenter.getListUserFromSearcjQuerry(text)
                }
                return true
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_chat, container, false)
    }
}