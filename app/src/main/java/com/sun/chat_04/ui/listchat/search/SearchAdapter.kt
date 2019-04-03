package com.sun.chat_04.ui.listchat.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.ui.listchat.search.SearchAdapter.ViewHolder
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.item_search_chat.view.textConnected
import kotlinx.android.synthetic.main.item_search_chat.view.textNameSearch

class SearchAdapter(
    private val lastMessages: ArrayList<LastMessage>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_chat, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return lastMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(lastMessages[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(lastMessage: LastMessage) {
            with(itemView) {
                textNameSearch.text = lastMessage.userName
                textConnected.text = Constants.CONNECTED
            }
        }
    }
}