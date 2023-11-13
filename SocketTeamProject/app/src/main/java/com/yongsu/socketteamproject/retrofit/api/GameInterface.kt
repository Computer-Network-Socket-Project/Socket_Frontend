package com.yongsu.socketteamproject.retrofit.api

import com.yongsu.socketteamproject.retrofit.model.GameInfoReq
import com.yongsu.socketteamproject.retrofit.model.GameInfoRes
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GameInterface {

    @GET("/test1")
    suspend fun getGameList(): List<GameInfoRes>

    @GET("/test2/{title}")
    suspend fun getGameData(@Path("title") title: String): List<GameInfoRes>

    @GET("test3")
    suspend fun getGame(): Response<ResponseBody>

    @POST("/test3")
    suspend fun postGame(@Body gameData: GameInfoReq): ResponseBody

}