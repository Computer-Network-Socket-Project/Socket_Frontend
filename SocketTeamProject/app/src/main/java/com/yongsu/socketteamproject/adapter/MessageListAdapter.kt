package com.yongsu.socketteamproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yongsu.socketteamproject.databinding.MessageListItemBinding
import com.yongsu.socketteamproject.viewmodel.MessageListItem

class MessageListAdapter:
    ListAdapter<MessageListItem, MessageListAdapter.MessageListViewHolder>(DiffCallback)  {

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<MessageListItem>(){
            override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MessageListViewHolder(private val binding : MessageListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(MessageListItem: MessageListItem){
            binding.messageTV.text = MessageListItem.message

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        val binding = MessageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



}