package com.yongsu.socketteamproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yongsu.socketteamproject.databinding.ActivityGameSearchResultBinding

class GameSearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameSearchResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_search_result)

    }
}