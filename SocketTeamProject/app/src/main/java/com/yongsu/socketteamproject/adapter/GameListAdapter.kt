package com.yongsu.socketteamproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yongsu.socketteamproject.GameListClickListener
import com.yongsu.socketteamproject.viewmodel.GameListItem
import com.yongsu.socketteamproject.R
import com.yongsu.socketteamproject.databinding.GameListItemBinding


class GameListAdapter(private val gameListClickListener: GameListClickListener) :
    ListAdapter<GameListItem, GameListAdapter.GameListViewHolder>(DiffCallback) {

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<GameListItem>(){
            override fun areItemsTheSame(oldItem: GameListItem, newItem: GameListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GameListItem, newItem: GameListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class GameListViewHolder(private val binding : GameListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(gameListItem: GameListItem){
            binding.gameTitle.text = gameListItem.title
            binding.firstTeam.text = gameListItem.firstTeam
            binding.secondTeam.text = gameListItem.secondTeam
            binding.firstTeamScore.text = gameListItem.firstTeamScore.toString()
            binding.secondTeamScore.text = gameListItem.secondTeamScore.toString()
            binding.gameStatus.text = gameListItem.gameStatus
            binding.likeAmount.text = "+ " + gameListItem.likeAmount.toString()
            if(gameListItem.isSoccer){
                binding.ballImageView.setImageResource(R.drawable.soccerball)
            }
            if(!gameListItem.isOnAir){
                binding.onAirView.setVisibility(View.GONE)
            }

            binding.goods.setOnClickListener {
                gameListClickListener?.onGameListTouch(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val binding = GameListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}