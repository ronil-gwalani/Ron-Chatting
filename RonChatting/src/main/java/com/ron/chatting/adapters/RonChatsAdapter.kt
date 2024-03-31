package com.ron.chatting.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.ron.chatting.databinding.ReciverchatlayoutBinding
import com.ron.chatting.databinding.SenderchatlayoutBinding
import com.ron.chatting.helpers.dateTimeFromTimeStamp
import com.ron.chatting.models.RonMessageModel

internal class RonChatsAdapter(
    private var list: ArrayList<RonMessageModel>,
    private var senderID: String,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val SENDER_TYPE = 1
    private val RECEIVER_TYPE = 2
    fun addMessage(messageModel: RonMessageModel) {
        list.add(messageModel)
        notifyItemInserted(list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDER_TYPE) {
            SenderHolder(
                SenderchatlayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceiverHolder(
                ReciverchatlayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId
                ?.startsWith(
                    senderID
                ) == true
        ) {
            SENDER_TYPE
        } else {
            RECEIVER_TYPE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val message = list[holder.adapterPosition]
        if (holder.javaClass == SenderHolder::class.java) {
            with((holder as SenderHolder).binding) {
                txtMessage.text = message.message
//                txtMessage.setTrimLines(6)
//                txtMessage.setExpandedTextColor(R.color.receiverChatBoxColor)
//                txtMessage.setExpandedTextColor(R.color.receiverChatBoxColor)
                messageTiming.text = dateTimeFromTimeStamp(message.timeStamp ?: "")
                if (messageTiming.text.toString().isNotEmpty()) {
                    frame.showTimingListener(messageTiming)
                }
            }
        } else {
            with((holder as ReceiverHolder).binding) {
                txtMessage.text = message.message
//                txtMessage.setTrimLines(6)
//                txtMessage.setExpandedTextColor(R.color.senderChatBoxColor)
//                txtMessage.setCollapsedTextColor(R.color.senderChatBoxColor)

                messageTiming.text = dateTimeFromTimeStamp(message.timeStamp ?: "")
                if (messageTiming.text.toString().isNotEmpty()) {
                    frame.showTimingListener(messageTiming)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ReceiverHolder(val binding: ReciverchatlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class SenderHolder(val binding: SenderchatlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    @SuppressLint("ClickableViewAccessibility")
    private fun FrameLayout.showTimingListener(timingLayout: View) {
        this.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    timingLayout.visibility = View.VISIBLE
                    true
                }

                MotionEvent.ACTION_UP -> {
                    timingLayout.visibility = View.GONE
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    timingLayout.visibility = View.VISIBLE
                    true
                }

                else -> {
                    timingLayout.visibility = View.GONE
                    false
                }

            }
        }
    }


}
