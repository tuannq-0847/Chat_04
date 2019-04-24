package com.sun.chat_04.ui.discovery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.model.UserDistanceWrapper
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.item_discovery.view.imageGender
import kotlinx.android.synthetic.main.item_discovery.view.imageUserDiscovery
import kotlinx.android.synthetic.main.item_discovery.view.textDistanceItem
import kotlinx.android.synthetic.main.item_discovery.view.textGenderDiscovery
import kotlinx.android.synthetic.main.item_discovery.view.textNameUserDiscovery

class DiscoveryAdapter(
    var userDistanceWrappers: List<UserDistanceWrapper>,
    val userClickListener: (user: User) -> Unit
) :
    RecyclerView.Adapter<DiscoveryAdapter.DiscoveryViewHolder>() {

    fun refreshUsers(userDistanceWrappers: List<UserDistanceWrapper>) {
        this.userDistanceWrappers = userDistanceWrappers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DiscoveryViewHolder {
        return DiscoveryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_discovery, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userDistanceWrappers.size
    }

    override fun onBindViewHolder(holder: DiscoveryViewHolder, position: Int) {
        holder.bindView(userDistanceWrappers[position])
        if (userDistanceWrappers[position].distance >= 0) {
            holder.displayDistance(userDistanceWrappers[position].distance)
        } else {
            holder.hideDistance()
        }
    }

    inner class DiscoveryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(userDistanceWrapper: UserDistanceWrapper) {
            with(itemView) {
                displayUserAvatar(userDistanceWrapper.user.pathAvatar, imageUserDiscovery)
                textNameUserDiscovery.text = userDistanceWrapper.user.userName
                displayGender(userDistanceWrapper.user.gender)
                imageGender.setImageResource(
                    when (userDistanceWrapper.user.gender) {
                        Constants.MALE -> R.drawable.ic_male
                        else -> R.drawable.ic_female
                    }
                )
                setOnClickListener { userClickListener(userDistanceWrapper.user) }
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

        fun displayDistance(distance: Float) {
            itemView.textDistanceItem?.let {
                it.visibility = View.VISIBLE
                val noti = "$distance km"
                it.text = noti
            }
            itemView.textDistanceItem?.visibility = View.VISIBLE
        }

        fun hideDistance() {
            itemView.textDistanceItem?.visibility = View.INVISIBLE
            itemView.textDistanceItem?.visibility = View.INVISIBLE
        }
    }
}
