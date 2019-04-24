package com.sun.chat_04.ui.profile

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import kotlinx.android.synthetic.main.item_image.view.imageViewImage

class ImageAdapter(private var images: List<String>, private val imageOnClick: (uri: String) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    fun refreshImages(images: List<String>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (images.isNullOrEmpty()) {
            return 0
        }
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(uri: String) {
            with(itemView) {
                imageViewImage?.apply {
                    displayImage(Uri.parse(uri), this)
                }
                setOnClickListener { imageOnClick(uri) }
            }
        }

        private fun displayImage(uri: Uri, imageView: ImageView) {
            Glide.with(imageView)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(imageView)
        }
    }
}
