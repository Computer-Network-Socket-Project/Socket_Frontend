package com.yongsu.socketteamproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yongsu.socketteamproject.adapter.MessageListAdapter
import com.yongsu.socketteamproject.databinding.ActivityShowGameBinding
import com.yongsu.socketteamproject.viewmodel.MessageListItem
import com.yongsu.socketteamproject.viewmodel.ViewerClientThread

class ShowGameActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding : ActivityShowGameBinding

    private val MessageAdapter = MessageListAdapter()

    private var isFirstHalf = true
    private val isAdmin = true
    private var isSoccer = false
    private var isSave = false
    private var whichTeam = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_game)

        /*
        val viewerThread = ViewerClientThread(this@ShowGameActivity)
        viewerThread.start()
         */
        initView()
    }

    private fun initView(){
        with(binding){

            // 뒤로가기
            backView.setOnClickListener {
                finish()
            }

            // 전반 후반
            firstHalfTV.setOnClickListener {
                isFirstHalf = true
                gameStatusTV.text = "전반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
                MessageAdapter.submitList(FirstDummyDate())
            }
            secondHalfTV.setOnClickListener {
                isFirstHalf = false
                gameStatusTV.text = "후반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26_white)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26)
                MessageAdapter.submitList(SecondDummyDate())
            }

            // 리사이클러뷰
            gameRV.adapter = MessageAdapter
            val manager = LinearLayoutManager(this@ShowGameActivity)
            manager.reverseLayout = true
            manager.stackFromEnd = true
            gameRV.layoutManager = manager
            if(isFirstHalf){
                MessageAdapter.submitList(FirstDummyDate())
            }else{
                MessageAdapter.submitList(SecondDummyDate())
            }

        }
    }

    fun receiveMatchInfo(title: String, team1: String, team2: String) {
        binding.titleTV.text = title
        binding.firstTeam.text = team1
        binding.secondTeam.text = team2
    }
    fun receiveScore(firstScore: Int, secondScore: Int) {
        binding.firstTeamScore.text = firstScore.toString()
        binding.secondTeamScore.text = secondScore.toString()
    }
    fun receiveHalfStatus(gameStatus: Boolean) {
        if(gameStatus){
            binding.gameStatusTV.text = "전반전"
            binding.firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
            binding.secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
            MessageAdapter.submitList(FirstDummyDate())
        }else{
            isFirstHalf = false
            binding.gameStatusTV.text = "후반전"
            binding.firstHalfTV.setBackgroundResource(R.drawable.left_round_26_white)
            binding.secondHalfTV.setBackgroundResource(R.drawable.right_round_26)
            MessageAdapter.submitList(SecondDummyDate())
        }
    }

    private fun FirstDummyDate() : ArrayList<MessageListItem>{
        val dummy1 = MessageListItem(1, "전반전 시작합니다!")
        val dummy2 = MessageListItem(2, "비상, 다리우스의 환성적인 덩크")
        val dummy3 = MessageListItem(3, "비상, 아무무 투입, 드레이븐 교체")
        val dummy4 = MessageListItem(4, "라온, 이회장의 그림같은 3점슛")
        val dummy5 = MessageListItem(5, "비상, 유회장의 파리채 블로킹")
        val dummy6 = MessageListItem(6, "라온, 피즈의 돌파 레이업")
        val dummy7 = MessageListItem(7, "라온, 피즈의 앤드원!!")

        val arr = ArrayList<MessageListItem>()
        arr.add(dummy1)
        arr.add(dummy2)
        arr.add(dummy3)
        arr.add(dummy4)
        arr.add(dummy5)
        arr.add(dummy6)
        arr.add(dummy7)

        return arr
    }

    private fun SecondDummyDate() : ArrayList<MessageListItem>{
        val dummy1 = MessageListItem(1, "후반전 시작합니다!")
        val dummy2 = MessageListItem(2, "라온, 벨베스의 안아줘요")
        val dummy3 = MessageListItem(3, "라온, 벨코즈 파울 아웃, 그브 투입")
        val dummy4 = MessageListItem(4, "비상, 임난쟁의 딥쓰리!!")
        val dummy5 = MessageListItem(5, "비상, 아카시의 앵클 브레이커")
        val dummy6 = MessageListItem(6, "라온, 람머스의 구른다")
        val dummy7 = MessageListItem(7, "라온, 리볼버의 와웅")

        val arr = ArrayList<MessageListItem>()
        arr.add(dummy1)
        arr.add(dummy2)
        arr.add(dummy3)
        arr.add(dummy4)
        arr.add(dummy5)
        arr.add(dummy6)
        arr.add(dummy7)

        return arr
    }


}