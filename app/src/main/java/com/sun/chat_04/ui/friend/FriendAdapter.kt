package com.sun.chat_04.ui.friend

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.friend.FriendAdapter.ViewHolder
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.items_friends.view.imageStatusUser
import kotlinx.android.synthetic.main.items_friends.view.imageUserLastMessage
import kotlinx.android.synthetic.main.items_friends.view.textLastMessage
import kotlinx.android.synthetic.main.items_friends.view.textUserRec

class FriendAdapter(
    private val friends: ArrayList<Friend>,
    private val listener: (friend: Friend) -> Unit
) :
    Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_friends, parent, false)
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
                textUserRec.text = friend.userName
                textLastMessage.text = friend.contents
                Glide.with(context)
                    .load(friend.avatarLink)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .into(imageUserLastMessage)
                if (friend.isonline == Constants.ONLINE) {
                    imageStatusUser.setBackgroundResource(R.drawable.background_online)
                } else {
                    imageStatusUser.setBackgroundResource(R.drawable.background_offline)
                }
                setOnClickListener { listener(friend) }

            }
        }
    }
}
