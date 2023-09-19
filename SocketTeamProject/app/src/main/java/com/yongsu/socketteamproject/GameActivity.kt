package com.yongsu.socketteamproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yongsu.socketteamproject.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameBinding

    private var isFirstHalf = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        initView()
    }

    private fun initView(){
        with(binding){
            firstTeamUp.setOnClickListener {
                val addNum = firstTeamScore.text.toString().toInt() + 1
                firstTeamScore.setText(addNum.toString())
            }
            firstTeamDown.setOnClickListener {
                val addNum = firstTeamScore.text.toString().toInt() - 1
                if(addNum >= 0){
                    firstTeamScore.setText(addNum.toString())
                }
            }
            secondTeamUp.setOnClickListener {
                val addNum = secondTeamScore.text.toString().toInt() + 1
                secondTeamScore.setText(addNum.toString())
            }
            secondTeamDown.setOnClickListener {
                val addNum = secondTeamScore.text.toString().toInt() - 1
                if(addNum >= 0){
                    secondTeamScore.setText(addNum.toString())
                }
            }

            firstHalfTV.setOnClickListener {
                isFirstHalf = true
                gameStatusTV.text = "전반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
            }
            secondHalfTV.setOnClickListener {
                isFirstHalf = false
                gameStatusTV.text = "후반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26_white)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26)
            }

        }
    }
}