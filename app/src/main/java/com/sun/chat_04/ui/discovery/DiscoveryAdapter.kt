package com.sun.chat_04.ui.discovery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import kotlinx.android.synthetic.main.item_discovery.view.textNameUserDiscovery

class DiscoveryAdapter(var users: List<User>, val userClickListener: (user: User) -> Unit) :
    RecyclerView.Adapter<DiscoveryAdapter.DiscoveryViewHoler>() {

    fun refreshAllUsers(user: List<User>) {
        users = user
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
                textNameUserDiscovery.setText(user.userName)
                setOnClickListener { userClickListener(user) }
            }
        }
    }
}
