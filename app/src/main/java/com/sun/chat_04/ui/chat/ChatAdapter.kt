package com.sun.chat_04.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.items_chat_rec.view.textMessageReceiver
import kotlinx.android.synthetic.main.items_chat_send.view.textMessageSend
import kotlinx.android.synthetic.main.items_image_rec.view.imageRec
import kotlinx.android.synthetic.main.items_image_rec.view.imageUserRec
import kotlinx.android.synthetic.main.items_image_send.view.imageSend

class ChatAdapter(
    private val idUser: String?,
    private val messages: ArrayList<Message>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_rec, parent, false)
        when (viewType) {
            Constants.USER_REC -> return RecViewHolder(view)
            Constants.USER_SEND -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_send, parent, false)
                return SendViewHolder(view)
            }
            Constants.IMAGE_USER_SEND -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_image_send, parent, false)
                return SendViewHolderImage(view)
            }
            Constants.IMAGE_USER_REC -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_image_rec, parent, false)
                return RecViewHolderImage(view)
            }
        }
        return SendViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        when {
            messages[position].from == idUser
                    && messages[position].type == Constants.TEXT_MESSAGE
            -> return Constants.USER_SEND
            messages[position].from != idUser
                    && messages[position].type == Constants.TEXT_MESSAGE
            -> return Constants.USER_REC
            messages[position].from != idUser
                    && messages[position].type == Constants.IMAGE_MESSAGE
            -> return Constants.IMAGE_USER_REC
            messages[position].from == idUser
                    && messages[position].type == Constants.IMAGE_MESSAGE
            -> return Constants.IMAGE_USER_SEND
        }
        return Constants.USER_SEND
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when {
            holder.itemViewType == Constants.USER_SEND -> {
                val holderSend = holder as SendViewHolder
                holderSend.onBind(message)
            }
            holder.itemViewType == Constants.USER_REC -> {
                val holderRec = holder as RecViewHolder
                holderRec.onBind(message)
            }
            holder.itemViewType == Constants.IMAGE_USER_REC -> {
                val holderImageRec = holder as RecViewHolderImage
                holderImageRec.onBind(message)
            }
            holder.itemViewType == Constants.IMAGE_USER_SEND -> {
                val holderImageSend = holder as SendViewHolderImage
                holderImageSend.onBind(message)
            }
        }
    }

    inner class RecViewHolderImage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                Glide.with(context)
                    .load(message.contents)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageRec)
                imageUserRec.setBackgroundResource(R.drawable.avatar)
            }
        }
    }

    inner class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                textMessageReceiver.text = message.contents
            }
        }
    }

    inner class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                textMessageSend.text = message.contents
            }
        }
    }

    inner class SendViewHolderImage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                Glide.with(context)
                    .load(message.contents)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageSend)
            }
        }
    }
}
