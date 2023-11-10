package com.yongsu.socketteamproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yongsu.socketteamproject.adapter.MessageListAdapter
import com.yongsu.socketteamproject.databinding.ActivityGameBinding
import com.yongsu.socketteamproject.viewmodel.GameListItem
import com.yongsu.socketteamproject.viewmodel.MessageListItem
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameBinding

    private var isFirstHalf = true
    private val isAdmin = true
    private var isSoccer = false
    private var isSave = false
    private var whichTeam = 0

    // about socket
    private lateinit var createrClientThread: CreaterClientThread
    var mHandler: Handler? = null
    var socket: Socket? = null
    var outstream: DataOutputStream? = null
    var instream: DataInputStream? = null
    var newip: String? = "192.168.44.195"
    var port = 9999

    private val MessageAdapter = MessageListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        // 팀 선택해서 올리기
        binding.teamMessage.text = binding.firstTeam.text.toString()
        binding.teamMessage.setOnClickListener {
            if(whichTeam % 3 == 0){
                binding.teamMessage.text = binding.firstTeam.text.toString()
                whichTeam++
            } else if(whichTeam % 3 == 1){
                binding.teamMessage.text = binding.secondTeam.text.toString()
                whichTeam++
            } else{
                binding.teamMessage.text = "X"
                whichTeam = 0
            }
        }

        /*
        createrClientThread = CreaterClientThread()
        createrClientThread.start()

         */

        // 제목, 팀명 업데이트
        var title = binding.titleTV.text.toString()
        var team1 = binding.firstTeam.text.toString()
        var team2 = binding.secondTeam.text.toString()
        //createrClientThread.sendMatchInfo(title, team1, team2)

        initBack()
        initGameStatus()
        initMessage()
        initScore()
    }

    private fun initBack(){
        with(binding){
            // 뒤로 가기
            backView.setOnClickListener {
                // 저장이 된 경우에만 finish()로 바로 넘어감
                // admin이 아니면 저장 불가
                if(isSave && !isAdmin){ // 저장이 됨
                    finish()
                }else{      // 저장 안됨
                    AlertDialog.Builder(this@GameActivity)
                        .setTitle("${firstTeam.text} vs ${secondTeam.text}")
                        .setMessage("중계를 등록하시겠습니까?\n(등록하지 않은 중계는 영구 삭제됩니다.)")
                        .setPositiveButton("네", object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface, which: Int){
                                Toast.makeText(applicationContext, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        })
                        .setNegativeButton("아니오", object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface, which: Int){
                                Toast.makeText(applicationContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        })
                        .create()
                        .show()
                }
            }
        }
    }

    private fun initScore(){
        with(binding){
            // 점수 올리고 내리고
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

            // 모든 팀의 점수를 update
            val fTeamScore = firstTeamScore.text.toString().toInt()
            val sTeamScore = secondTeamScore.text.toString().toInt()
            //createrClientThread.updateScore(fTeamScore, sTeamScore)
        }
    }

    private fun initGameStatus(){
        with(binding){
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

            // 전후반 상태 전달
            //createrClientThread.setHalf(isFirstHalf)
        }
    }

    private fun initMessage(){
        with(binding){
            // 리사이클러뷰
            gameRV.adapter = MessageAdapter
            val manager = LinearLayoutManager(this@GameActivity)
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