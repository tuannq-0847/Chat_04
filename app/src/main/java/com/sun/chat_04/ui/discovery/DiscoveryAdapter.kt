package com.sun.chat_04.ui.discovery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.item_discovery.view.imageGender
import kotlinx.android.synthetic.main.item_discovery.view.imageUserDiscovery
import kotlinx.android.synthetic.main.item_discovery.view.textGenderDiscovery
import kotlinx.android.synthetic.main.item_discovery.view.textNameUserDiscovery

class DiscoveryAdapter(var users: List<User>, val userClickListener: (user: User) -> Unit) :
    RecyclerView.Adapter<DiscoveryAdapter.DiscoveryViewHoler>() {

    fun refreshUsers(user: List<User>) {
        users = user
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DiscoveryViewHoler {
        return DiscoveryViewHoler(
            LayoutInflater.from(parent.context).inflate(R.layout.item_discovery, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: DiscoveryViewHoler, position: Int) {
        holder.bindView(users[position])
    }

    inner class DiscoveryViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(user: User) {
            with(itemView) {
                displayUserAvatar(user.pathAvatar, imageUserDiscovery)
                textNameUserDiscovery.text = user.userName
                displayGender(user.gender)
                imageGender.setImageResource(
                    when (user.gender) {
                        Constants.MALE -> R.drawable.ic_male
                        else -> R.drawable.ic_female
                    }
                )
                setOnClickListener { userClickListener(user) }
            }
        }

        private fun displayGender(gender: String?) {
            if (!gender.isNullOrEmpty()) {
                with(itemView) {
                    textGenderDiscovery.text = when (gender) {
                        Constants.MALE -> resources.getString(R.string.male)
                        else -> resources.getString(R.string.female)
                    }
                }
            }
        }

        private fun displayUserAvatar(pathImage: String, imageView: ImageView) {
            Glide.with(imageView)
                .load(pathImage)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(imageView)
        }
    }
}
