package com.yongsu.socketteamproject

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yongsu.socketteamproject.adapter.MessageListAdapter
import com.yongsu.socketteamproject.databinding.ActivityGameBinding
import com.yongsu.socketteamproject.retrofit.RetrofitInstance
import com.yongsu.socketteamproject.retrofit.api.GameInterface
import com.yongsu.socketteamproject.retrofit.model.GameInfoReq
import com.yongsu.socketteamproject.viewmodel.CreaterClientThread
import com.yongsu.socketteamproject.viewmodel.MessageListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class GameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameBinding

    private var isFirstHalf = 0
    private val isAdmin = true
    private var isBasket = 0
    private var isSave = false
    private var whichTeam = 0

    // about socket
    private lateinit var createrClientThread: CreaterClientThread

    private val MessageAdapter = MessageListAdapter()
    private val service = RetrofitInstance.getInstance().create(GameInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        initStart()

        // 팀 선택해서 올리기
        // 프론트에서 teamMessage.text와 messageEt.text를 합쳐서 서버에 보내야함
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


        initMessage()

        val tcpConnect = lifecycleScope.launch {
            initSetTCP()

            Log.d("에러찾기", "여기냐?")
        }



        lifecycleScope.launch{
            Log.d("에러찾기", "조인 전인데")
            tcpConnect.join()
            Log.d("에러찾기", "조인 후인데")

            initBack()
            initGameStatus()
            initScore()
            initSetSport()

            try{
                binding.titleTV.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        try{
                            Log.d("에러찾기", "함수 호출 전")
                            initSetTitle()
                            Log.d("에러찾기", "함수 호출 후")
                        }catch(e : IOException){
                            Log.e("에러찾기", "들어와서 $e")
                        }
                    }
                })
            }catch(e: IOException){
                Log.e("에러찾기", "들어오기 전에 $e")
            }

            try{
                binding.firstTeam.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        try{
                            Log.d("에러찾기", "함수 호출 전")
                            initSetTitle()
                            Log.d("에러찾기", "함수 호출 후")
                        }catch(e : IOException){
                            Log.e("에러찾기", "들어와서 $e")
                        }
                    }
                })
            }catch(e: IOException){
                Log.e("에러찾기", "들어오기 전에 $e")
            }

            try{
                binding.secondTeam.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        try{
                            Log.d("에러찾기", "함수 호출 전")
                            initSetTitle()
                            Log.d("에러찾기", "함수 호출 후")
                        }catch(e : IOException){
                            Log.e("에러찾기", "들어와서 $e")
                        }
                    }
                })
            }catch(e: IOException){
                Log.e("에러찾기", "들어오기 전에 $e")
            }

        }

    }

    private fun initSetSport(){
        // 메뉴 누르면 농구, 축구 선택창 뜸
        binding.menuView.setOnClickListener {
            binding.sportLayout.visibility = View.VISIBLE
        }

        // 농구 누르면
        binding.sportBascket.setOnClickListener {
            binding.sportLayout.visibility = View.GONE
            // sport를 1로 설정
            lifecycleScope.launch(Dispatchers.IO){
                try{
                    createrClientThread.updateScore(binding.titleTV.text.toString(),
                        binding.firstTeam.text.toString(),
                        binding.secondTeam.text.toString(),
                        binding.firstTeamScore.text.toString().toInt(),
                        binding.secondTeamScore.text.toString().toInt(),
                        isFirstHalf,
                        1
                    )
                }catch(e : IOException){
                    Log.e("에러찾기", "함수에 들어옴 : $e")
                }

            }
        }

        // 축구 누르면
        binding.sportSoccer.setOnClickListener {
            binding.sportLayout.visibility = View.GONE

            // sport를 0으로 설정
            lifecycleScope.launch(Dispatchers.IO){
                try{
                    createrClientThread.updateScore(binding.titleTV.text.toString(),
                        binding.firstTeam.text.toString(),
                        binding.secondTeam.text.toString(),
                        binding.firstTeamScore.text.toString().toInt(),
                        binding.secondTeamScore.text.toString().toInt(),
                        isFirstHalf,
                        0
                    )
                }catch(e : IOException){
                    Log.e("에러찾기", "함수에 들어옴 : $e")
                }

            }
        }
    }

    private fun initSetTitle(){
        lifecycleScope.launch(Dispatchers.IO){
            // 제목, 팀명 업데이트
            var title = binding.titleTV.text.toString()
            var team1 = binding.firstTeam.text.toString()
            var team2 = binding.secondTeam.text.toString()

            try{
                createrClientThread.updateScore(title,
                    team1,
                    team2,
                    binding.firstTeamScore.text.toString().toInt(),
                    binding.secondTeamScore.text.toString().toInt(),
                    isFirstHalf,
                    isBasket
                )
            }catch(e : IOException){
                Log.e("에러찾기", "함수에 들어옴 : $e")
            }

        }
    }

    private suspend fun initSetTCP() = withContext(Dispatchers.IO) {
        try{
            createrClientThread = CreaterClientThread()
            createrClientThread.start()
        }catch(e : IOException){
            Log.e("TCP 통신", "$e")
        }
    }


    private fun initStart(){
        lifecycleScope.launch {
            try {
                val response = service.getGame()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("http 통신 GameActivity", responseBody.string())
                    } else {
                        Log.e("http 통신 GameActivity", "Response body is null")
                    }
                } else {
                    Log.e("http 통신 GameActivity", "Request failed with code: ${response.code()}")
                }
            } catch(e: Exception) {
                Log.e("http 통신 GameActivity", "야호 $e")
            }
        }
    }

    private fun initEnd(){
        val endClick = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val gameData = GameInfoReq(binding.titleTV.text.toString(),
                    binding.firstTeam.text.toString(),
                    binding.firstTeamScore.text.toString().toInt(),
                    binding.secondTeam.text.toString(),
                    binding.secondTeamScore.text.toString().toInt(),
                    1, 1, 1)  // 원하는 데이터로 채웁니다.
                val response = service.postGame(gameData)
                Log.d("http 통신 GameActivity", "이곳은 $response")
            } catch(e: Exception) {
                Log.e("http 통신 GameActivity", "이곳은 $e")
            }
        }

        lifecycleScope.launch(Dispatchers.IO){
            endClick.join()
            //Toast.makeText(applicationContext, "중계가 등록되었습니다.", Toast.LENGTH_LONG).show()
            finish()
        }

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
                        .setMessage("중계를 종료하시겠습니까?")
                        .setPositiveButton("네", object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface, which: Int){
                                val closeSocket = lifecycleScope.launch(Dispatchers.IO){
                                    createrClientThread.closeCreaterSocket()
                                }
                                lifecycleScope.launch(Dispatchers.IO){
                                    closeSocket.join()
                                    initEnd()
                                }



                            }
                        })
                        .setNegativeButton("아니오", object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface, which: Int){

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
                scoreChange(firstTeamScore.text.toString().toInt(), secondTeamScore.text.toString().toInt())
            }
            firstTeamDown.setOnClickListener {
                val addNum = firstTeamScore.text.toString().toInt() - 1
                if(addNum >= 0){
                    firstTeamScore.setText(addNum.toString())
                }
                scoreChange(firstTeamScore.text.toString().toInt(), secondTeamScore.text.toString().toInt())
            }
            secondTeamUp.setOnClickListener {
                val addNum = secondTeamScore.text.toString().toInt() + 1
                secondTeamScore.setText(addNum.toString())
                scoreChange(firstTeamScore.text.toString().toInt(), secondTeamScore.text.toString().toInt())
            }
            secondTeamDown.setOnClickListener {
                val addNum = secondTeamScore.text.toString().toInt() - 1
                if(addNum >= 0){
                    secondTeamScore.setText(addNum.toString())
                }
                scoreChange(firstTeamScore.text.toString().toInt(), secondTeamScore.text.toString().toInt())
            }
        }
    }

    private fun scoreChange(fTeamScore: Int, sTeamScore: Int){
        // 모든 팀의 점수를 update
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                createrClientThread.updateScore(binding.titleTV.text.toString(),
                    binding.firstTeam.text.toString(),
                    binding.secondTeam.text.toString(),
                    fTeamScore,
                    sTeamScore,
                    isFirstHalf,
                    isBasket
                )
            }catch (e : IOException){
                Log.e("TCP 통신", "점수 : $e")
            }
        }
    }

    private fun initGameStatus(){
        with(binding){
            // 전반 후반
            firstHalfTV.setOnClickListener {
                isFirstHalf = 0
                gameStatusTV.text = "전반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26_white)
                MessageAdapter.submitList(FirstDummyDate())

                // 전후반 상태 전달
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        createrClientThread.updateScore(binding.titleTV.text.toString(),
                            binding.firstTeam.text.toString(),
                            binding.secondTeam.text.toString(),
                            binding.firstTeamScore.text.toString().toInt(),
                            binding.secondTeamScore.text.toString().toInt(),
                            isFirstHalf,
                            isBasket
                        )
                    }catch (e : IOException){
                        Log.e("TCP 통신", "점수 : $e")
                    }
                }
            }
            secondHalfTV.setOnClickListener {
                isFirstHalf = 1
                gameStatusTV.text = "후반전"
                firstHalfTV.setBackgroundResource(R.drawable.left_round_26_white)
                secondHalfTV.setBackgroundResource(R.drawable.right_round_26)
                MessageAdapter.submitList(SecondDummyDate())

                // 전후반 상태 전달
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        createrClientThread.updateScore(binding.titleTV.text.toString(),
                            binding.firstTeam.text.toString(),
                            binding.secondTeam.text.toString(),
                            binding.firstTeamScore.text.toString().toInt(),
                            binding.secondTeamScore.text.toString().toInt(),
                            isFirstHalf,
                            isBasket
                        )
                    }catch (e : IOException){
                        Log.e("TCP 통신", "점수 : $e")
                    }
                }
            }



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
            if(isFirstHalf == 1){
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
//        arr.add(dummy1)
//        arr.add(dummy2)
//        arr.add(dummy3)
//        arr.add(dummy4)
//        arr.add(dummy5)
//        arr.add(dummy6)
//        arr.add(dummy7)

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
//        arr.add(dummy1)
//        arr.add(dummy2)
//        arr.add(dummy3)
//        arr.add(dummy4)
//        arr.add(dummy5)
//        arr.add(dummy6)
//        arr.add(dummy7)

        return arr
    }

}