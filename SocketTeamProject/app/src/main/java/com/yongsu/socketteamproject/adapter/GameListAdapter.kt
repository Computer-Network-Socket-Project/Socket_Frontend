package com.yongsu.socketteamproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yongsu.socketteamproject.viewmodel.GameListItem
import com.yongsu.socketteamproject.R
import com.yongsu.socketteamproject.databinding.GameListItemBinding


class GameListAdapter(private val gameList: ArrayList<GameListItem>) : RecyclerView.Adapter<GameListAdapter.GameListViewHolder>() {

    interface GameListClickListener {
        fun onGameListTouch(gameListItem: GameListItem)
    }

    private lateinit var GLClickListener: GameListClickListener

    fun setGLClickListener(itemClickListener: GameListClickListener) {
        GLClickListener = itemClickListener
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val binding = GameListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GameListViewHolder(binding)
    }

    override fun getItemCount(): Int = gameList.size

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(gameList[position])

        holder.itemView.setOnClickListener {
            GLClickListener.onGameListTouch(gameList[position])
        }
    }

}