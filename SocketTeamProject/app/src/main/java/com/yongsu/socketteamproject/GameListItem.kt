package com.yongsu.socketteamproject

data class GameListItem(
    val id : Int,
    val title : String,
    val firstTeam : String,
    val secondTeam : String,
    val firstTeamScore : Int,
    val secondTeamScore : Int,
    val gameStatus : String,
    val isOnAir : Boolean,
    val isSoccer : Boolean,
    val likeAmount : Int
)
