package com.sun.chat_04.ui.request

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.request.FriendRequestAdapter.ViewHolder
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.items_friend_request.view.buttonAccept
import kotlinx.android.synthetic.main.items_friend_request.view.buttonCancel
import kotlinx.android.synthetic.main.items_friend_request.view.imageAvatarUserRequest
import kotlinx.android.synthetic.main.items_friend_request.view.textGender
import kotlinx.android.synthetic.main.items_friend_request.view.textNameUserRequest

class FriendRequestAdapter(
    private val users: ArrayList<User>,
    private val listener: (user: User, id: Int) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_friend_request, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.onBind(user)
    }

    fun refreshFriendRequest() {
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(user: User) {
            with(itemView) {
                textNameUserRequest.text = user.userName
                if (user.gender == Constants.MALE) {
                    textGender.text = resources.getString(R.string.male)
                } else {
                    textGender.text = resources.getString(R.string.female)
                }
                Glide.with(context)
                    .load(user.pathAvatar)
                    .placeholder(R.drawable.avatar)
                    .centerCrop()
                    .into(imageAvatarUserRequest)
                buttonAccept.setOnClickListener {
                    listener(user, R.id.buttonAccept)
                }
                buttonCancel.setOnClickListener {
                    listener(user, R.id.buttonCancel)
                }
            }
        }
    }
}
