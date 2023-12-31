package com.yongsu.socketteamproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yongsu.socketteamproject.viewmodel.GameListItem
import com.yongsu.socketteamproject.R
import com.yongsu.socketteamproject.databinding.GameListItemBinding
import com.yongsu.socketteamproject.retrofit.model.GameInfoRes


class GameListAdapter(private val gameList: ArrayList<GameInfoRes>) : RecyclerView.Adapter<GameListAdapter.GameListViewHolder>() {

    interface GameListClickListener {
        fun onGameListTouch(gameListItem: GameInfoRes)
    }

    private lateinit var GLClickListener: GameListClickListener

    fun setGLClickListener(itemClickListener: GameListClickListener) {
        GLClickListener = itemClickListener
    }

    inner class GameListViewHolder(private val binding : GameListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(gameListItem: GameInfoRes){
            binding.gameTitle.text = gameListItem.gameName
            binding.firstTeam.text = gameListItem.team1Name
            binding.secondTeam.text = gameListItem.team2Name
            binding.firstTeamScore.text = gameListItem.team1Score
            binding.secondTeamScore.text = gameListItem.team2Score

            if(gameListItem.gameHalf == 1){
                binding.gameStatus.text = "후반전"
            }else{
                binding.gameStatus.text = "전반전"
            }

            binding.likeAmount.text = "+ " + gameListItem.greatNum.toString()
            if(gameListItem.sportType == 0){
                binding.ballImageView.setImageResource(R.drawable.soccerball)
            }else{
                binding.ballImageView.setImageResource(R.drawable.basketball)
            }
            // 0이면 진행 1이면 종료
            if(gameListItem.gameProgress == 1){
                binding.onAirView.setVisibility(View.GONE)
            }else{
                binding.onAirView.setVisibility(View.VISIBLE)
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