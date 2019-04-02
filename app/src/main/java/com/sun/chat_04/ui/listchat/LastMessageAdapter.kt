package com.sun.chat_04.ui.listchat

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.LastMessage
import com.sun.chat_04.ui.listchat.LastMessageAdapter.ViewHolder
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.items_list_message.view.imageStatusUser
import kotlinx.android.synthetic.main.items_list_message.view.textLastMessage
import kotlinx.android.synthetic.main.items_list_message.view.textUserRec

class LastMessageAdapter(
    private val LastMessages: ArrayList<LastMessage>,
    private val onClick: (massage: LastMessage) -> Unit
) :
    Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_list_message, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return LastMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(LastMessages[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(lastMessage: LastMessage) {
            with(itemView) {
                textUserRec.text = lastMessage.userName
                textLastMessage.text = lastMessage.contents
                if (lastMessage.isonline == Constants.ONLINE) {
                    imageStatusUser.setBackgroundResource(R.drawable.background_online)
                } else {
                    imageStatusUser.setBackgroundResource(R.drawable.background_offline)
                }
                setOnClickListener { onClick(lastMessage) }

            }
        }
    }
}
