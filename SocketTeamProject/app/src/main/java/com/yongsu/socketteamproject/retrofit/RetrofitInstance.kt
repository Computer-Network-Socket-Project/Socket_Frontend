package com.yongsu.socketteamproject.retrofit

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.NetworkInterface
import java.util.Collections

class RetrofitInstance {
    companion object{

        private var BASE_URL = "http://192.168.0.15:9999/"

        private var INSTANCE : Retrofit? = null

        fun getInstance() : Retrofit {
            Log.d("http통신", "$BASE_URL")

            if(INSTANCE == null){
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return INSTANCE!!
        }

        fun getLocalIpAddress(): String {
            try {
                for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    for (inetAddress in Collections.list(networkInterface.getInetAddresses())) {
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress.toString()
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("IP Address", "getLocalIpAddress", ex)
            }
            return ""
        }
    }
}