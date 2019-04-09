package com.sun.chat_04.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.items_chat_rec.view.editMessageReceiver
import kotlinx.android.synthetic.main.items_chat_send.view.editMessageSend

class ChatAdapter(
    private val idUser: String?,
    private val messages: ArrayList<Message>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_rec, parent, false)
        if (viewType == Constants.USER_REC) {
            return RecViewHolder(view)
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.items_chat_send, parent, false)
        return SendViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].from == idUser) {
            return Constants.USER_SEND
        } else {
            return Constants.USER_REC
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == Constants.USER_SEND) {
            val holderSend = holder as SendViewHolder
            holderSend.onBind(message)
        } else if (holder.itemViewType == Constants.USER_REC) {
            val holderRec = holder as RecViewHolder
            holderRec.onBind(message)
        }
    }

    inner class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                editMessageSend.text = message.contents
            }
        }
    }

    inner class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(message: Message) {
            with(itemView) {
                editMessageReceiver.text = message.contents
            }
        }
    }
}
