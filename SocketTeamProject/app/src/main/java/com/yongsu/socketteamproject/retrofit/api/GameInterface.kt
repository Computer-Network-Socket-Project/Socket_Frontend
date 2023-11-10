package com.yongsu.socketteamproject.retrofit.api

import com.yongsu.socketteamproject.retrofit.model.GameInfoRes
import retrofit2.Call
import retrofit2.http.GET

interface GameInterface {

    @GET("/test1")
    suspend fun getGameList(): List<GameInfoRes>

}