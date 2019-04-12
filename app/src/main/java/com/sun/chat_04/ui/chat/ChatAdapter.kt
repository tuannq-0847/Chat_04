package com.sun.chat_04.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.ui.chat.ChatAdapter.Companion.BaseViewHolder
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
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_rec, parent, false)
        return when (viewType) {
            USER_SEND -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_send, parent, false)
                SendViewHolder(view)
            }
            IMAGE_USER_SEND -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_image_send, parent, false)
                SendViewHolderImage(view)
            }
            IMAGE_USER_REC -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_image_rec, parent, false)
                RecViewHolderImage(view)
            }
            USER_REC -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_rec, parent, false)
                RecViewHolder(view)
            }
            else -> return RecViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].from == idUser
                    && messages[position].type == Constants.TEXT_MESSAGE
            -> USER_SEND
            messages[position].from != idUser
                    && messages[position].type == Constants.TEXT_MESSAGE
            -> USER_REC
            messages[position].from != idUser
                    && messages[position].type == Constants.IMAGE_MESSAGE
            -> IMAGE_USER_REC
            messages[position].from == idUser
                    && messages[position].type == Constants.IMAGE_MESSAGE
            -> IMAGE_USER_SEND
            else
            -> USER_REC
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val message = messages[position]
        holder.onBind(message)
    }

    inner class SendViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(message: Message) {
            with(itemView) {
                textMessageSend.text = message.contents
            }
        }
    }

    inner class RecViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(message: Message) {
            with(itemView) {
                textMessageReceiver.text = message.contents
            }
        }
    }

    inner class SendViewHolderImage(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(message: Message) {
            with(itemView) {
                Glide.with(context)
                    .load(message.contents)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageSend)
            }
        }
    }

    inner class RecViewHolderImage(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(message: Message) {
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

    companion object {
        const val USER_REC = 1
        const val USER_SEND = 0
        const val IMAGE_USER_REC = 2
        const val IMAGE_USER_SEND = 3

        abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            abstract fun onBind(message: Message)
        }
    }
}
