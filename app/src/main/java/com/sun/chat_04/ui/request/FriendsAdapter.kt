package com.sun.chat_04.ui.request

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.ui.request.FriendsAdapter.ViewHolder
import kotlinx.android.synthetic.main.item_friend.view.imageChatting
import kotlinx.android.synthetic.main.item_friend.view.imageFriends
import kotlinx.android.synthetic.main.item_friend.view.textDots
import kotlinx.android.synthetic.main.item_friend.view.textNameUser

class FriendsAdapter(private val friends: ArrayList<Friend>) : RecyclerView.Adapter<ViewHolder>() {
    private var onDeleteFriends: OnDeleteFriends? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false))
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.onBind(friend)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(friend: Friend) {
            with(itemView) {
                Glide.with(context)
                    .load(friend.avatarLink)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .into(imageFriends)
                textNameUser.text = friend.userName
                textDots.setOnClickListener {
                    openPopUpMenu(context, textDots, friend)
                }

                imageChatting.setOnClickListener {
                    onDeleteFriends?.onItemChatClicked(friend)
                }

                itemView.setOnClickListener {
                    onDeleteFriends?.onItemViewClicked(friend)
                }
            }
        }
    }

    fun setOnDeleteListener(onDeleteFriends: OnDeleteFriends) {
        this.onDeleteFriends = onDeleteFriends
    }

    fun openPopUpMenu(context: Context, textView: TextView, friend: Friend) {
        val popUp = PopupMenu(context, textView)
        popUp.inflate(R.menu.menu_friend)
        popUp.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuDelete -> {
                    onDeleteFriends?.onItemDeleteClicked(friend)
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popUp.show()
    }

    interface OnDeleteFriends {
        fun onItemDeleteClicked(friend: Friend)
        fun onItemChatClicked(friend: Friend)
        fun onItemViewClicked(friend: Friend)
    }
}
