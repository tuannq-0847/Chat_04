package com.sun.chat_04.ui.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.Message
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.items_chat_rec.view.edtMsgRec
import kotlinx.android.synthetic.main.items_chat_rec.view.imvAvtUserRec
import kotlinx.android.synthetic.main.items_chat_send.view.edtMsgSend

class AdapterChat(
    context: Context?, private val uid: String,
    private val listMessages: ArrayList<Message>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.items_chat_rec, parent, false)
        if (viewType == Constants.USER_REC) {
            return ViewHolderRec(view)
        }
        view = inflater.inflate(R.layout.items_chat_send, parent, false)
        return ViewHolderSend(view)
    }

    override fun getItemCount(): Int {
        return listMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listMessages[position].from == Global.firebaseAuth.currentUser?.uid.toString()) {
            return Constants.USER_SEND
        } else {
            return Constants.USER_REC
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = listMessages[position]
        if (holder.itemViewType == Constants.USER_SEND) {
            val holderSend = holder as ViewHolderSend
            holderSend.edtMsgSend.text = message.contents
        } else if (holder.itemViewType == Constants.USER_REC) {
            val holderRec = holder as ViewHolderRec
            holderRec.edtMsgRec.text = message.contents
        }
    }

    inner class ViewHolderSend(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtMsgSend = itemView.edtMsgSend
    }

    inner class ViewHolderRec(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtMsgRec = itemView.edtMsgRec
        val imvAvtUserRec = itemView.imvAvtUserRec
    }
}