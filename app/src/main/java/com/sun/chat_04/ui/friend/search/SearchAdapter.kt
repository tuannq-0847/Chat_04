package com.sun.chat_04.ui.friend.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.friend.search.SearchAdapter.ViewHolder
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.item_search_chat.view.textConnected
import kotlinx.android.synthetic.main.item_search_chat.view.textNameSearch

class SearchAdapter(
    private val friends: ArrayList<Friend>,
    private val listener: (massage: Friend) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_chat, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(friends[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(friend: Friend) {
            with(itemView) {
                textNameSearch.text = friend.userName
                textConnected.text = Constants.CONNECTED
                itemView.setOnClickListener { listener(friend) }
            }
        }
    }
}
