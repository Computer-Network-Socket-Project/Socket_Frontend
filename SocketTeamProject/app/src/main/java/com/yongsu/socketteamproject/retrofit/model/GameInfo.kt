package com.yongsu.socketteamproject.retrofit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class GameInfo(
    @SerializedName("id")
    val id: Int,

    // 나중에 에러나면 필드명이랑 매치 안되는거라 그러는듯
    @SerializedName("game_name")
    val gameName: String,

    @SerializedName("team1_name")
    val team1Name: String,

    @SerializedName("team1_score")
    val team1Score: String,

    @SerializedName("team2_name")
    val team2Name: String,

    @SerializedName("team2_score")
    val team2Score: String,

    @SerializedName("great_num")
    val greatNum: Int?,

    @SerializedName("sport_type")
    val sportType: String?,

    @SerializedName("game_datetime")
    val gameDateTime: Date?,

    @SerializedName("game_half")
    val gameHalf: Boolean?,

    @SerializedName("game_progress")
    val gameProgress: Boolean?
)
