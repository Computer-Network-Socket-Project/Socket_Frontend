package com.yongsu.socketteamproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yongsu.socketteamproject.adapter.MessageListAdapter
import com.yongsu.socketteamproject.databinding.ActivityShowGameBinding
import com.yongsu.socketteamproject.retrofit.model.GameInfoRes
import com.yongsu.socketteamproject.viewmodel.CreaterClientThread
import com.yongsu.socketteamproject.viewmodel.MessageListItem
import com.yongsu.socketteamproject.viewmodel.ViewerClientThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ShowGameActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding : ActivityShowGameBinding

    private val MessageAdapter = MessageListAdapter()

    private var isFirstHalf = 0

    private lateinit var viewerClientThread: ViewerClientThread

    private val gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_game)

        // 게임 데이터 받아오기
        val gameJson = intent.getStringExtra("gameData")
        val gameData = gson.fromJson(gameJson, GameInfoRes::class.java)

        if(gameData.gameProgress == 0){
            // 진행중인 경기라면 TCP 소켓 통신으로 실시간 통신
            val tcpConnect = lifecycleScope.launch(Dispatchers.IO) {
                initSetTCP()

                Log.d("엥?", "여기냐?")
            }

            lifecycleScope.launch(Dispatchers.IO){
                tcpConnect.join()
                initSetHTTP(gameData)
            }
        }else{
            initSetHTTP(gameData)
        }

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()

//        lifecycleScope.launch(Dispatchers.IO) {
//            viewerClientThread.closeViewerSocket()
//        }
    }

    private fun initSetHTTP(gameData: GameInfoRes){
        with(binding){
            titleTV.text = gameData.gameName
            firstTeam.text = gameData.team1Name
            secondTeam.text = gameData.team2Name
            firstTeamScore.text = gameData.team1Score
            secondTeamScore.text = gameData.team2Score
            if(gameData.gameHalf == 1){
                gameStatusTV.text = "후반전"
            }else{
                gameStatusTV.text = "전반전"
            }
        }
    }

    private fun initSetTCP(){
        lifecycleScope.launch(Dispatchers.IO){
            try {
                Log.d("엥?", "스레드 시작 전")
                viewerClientThread = ViewerClientThread(this@ShowGameActivity)
                viewerClientThread.start()
                Log.d("엥?", "스레드 시작됨")
            }catch(e : IOException){
                Log.e("엥?", "어디지 : $e")
            }
        }

    }

    private fun initView(){
        with(binding){

            // 뒤로가기
            backView.setOnClickListener {
                val killThreading = lifecycleScope.launch(Dispatchers.IO) {
                    viewerClientThread.closeViewerSocket()
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    killThreading.start()
                    killThreading.join()
                    finish()
                }

            }

            // 전반 후반
            firstHalfTV.setOnClickListener {
                isFirstHalf = 0
                gameStatusTV.text = "전반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
                MessageAdapter.submitList(FirstDummyDate())
            }
            secondHalfTV.setOnClickListener {
                isFirstHalf = 1
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
            if(isFirstHalf == 0){
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
    fun receiveScore(firstScore: String, secondScore: String) {
        binding.firstTeamScore.text = firstScore
        binding.secondTeamScore.text = secondScore
    }
    fun receiveHalfStatus(gameStatus: Int) {
        if(gameStatus == 0){
            isFirstHalf = 0
            binding.gameStatusTV.text = "전반전"
            binding.firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
            binding.secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
            MessageAdapter.submitList(FirstDummyDate())
        }else{
            isFirstHalf = 1
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