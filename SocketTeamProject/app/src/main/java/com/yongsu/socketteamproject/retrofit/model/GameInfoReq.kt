package com.yongsu.socketteamproject.retrofit.model

import com.google.gson.annotations.SerializedName

data class GameInfoReq(
    // 나중에 에러나면 필드명이랑 매치 안되는거라 그러는듯
    @SerializedName("game_name")
    val gameName: String,

    @SerializedName("team1_name")
    val team1Name: String,

    @SerializedName("team1_score")
    val team1Score: Int,

    @SerializedName("team2_name")
    val team2Name: String,

    @SerializedName("team2_score")
    val team2Score: Int,

    @SerializedName("sport_type")
    val sportType: Int?,

    @SerializedName("game_half")
    val gameHalf: Int?,

    @SerializedName("game_progress")
    val gameProgress: Int?
)
