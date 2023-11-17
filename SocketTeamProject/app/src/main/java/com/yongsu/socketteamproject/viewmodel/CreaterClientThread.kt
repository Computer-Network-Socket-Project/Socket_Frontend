package com.yongsu.socketteamproject.viewmodel

import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CreaterClientThread : Thread() {
    private var socket: Socket? = null
    private var outstream: DataOutputStream? = null
    private var instream: DataInputStream? = null

    private var isSocket = false

    init {
        Log.d("TCP 통신", "소켓 생성 전")
        try{
            socket = Socket("192.168.0.3", 8888)
            outstream = DataOutputStream(socket!!.getOutputStream())
            instream = DataInputStream(socket!!.getInputStream())
            Log.d("TCP 통신", "소켓 생성 후")
        }catch(e : IOException){
            Log.e("TCP 통신", "$e")
        }

    }

    // 데이터 변경과정
    // 받은 데이터 => json형식 => byte 배열

    // 제목, 팀명 업데이트
    fun sendMatchInfo(title: String, team1: String, team2: String) {
        val json = JSONObject()
        json.put("type", "match_info")
        json.put("game_name", title)
        json.put("team1_name", team1)
        json.put("team2_name", team2)

        sendMessage(json.toString())
    }

    // 점수 업데이트
    fun updateScore(title: String, team1: String, team2: String, scoreFirst: Int, scoreSecond: Int, half: Int) {
        val json = JSONObject()
        json.put("type", "creater") // creater인지 viewer인지
        json.put("action", "update_data")   // 어떤 작업을 하라고 보낼건지
        json.put("viewer_on", 1)    // 1을 보내면 데이터를 서버에서 Viewer로 보내라
        json.put("game_name", title)
        json.put("team1_name", team1)
        json.put("team2_name", team2)
        json.put("team1_score", scoreFirst)  // 'score'를 'team1_score'로 변경
        json.put("team2_score", scoreSecond)  // 'score'를 'team2_score'로 변경
        json.put("sport_type", 0)
        json.put("game_half", half)
        json.put("game_progress", 0)

        sendMessage(json.toString())
    }

    // 전후반 업데이트
    fun setHalf(half: Boolean) {
        val json = JSONObject()
        json.put("type", "half")
        json.put("game_half", half)

        sendMessage(json.toString())
    }

    // 문자 중계
    fun sendCommentary(comment: String) {
        val json = JSONObject()
        json.put("type", "commentary")
        json.put("comment", comment)

        sendMessage(json.toString())
    }

    fun closeCreaterSocket() {
        try {
            outstream?.close()
            instream?.close()
            socket?.close()
            isSocket = false
            Log.d("TCP 통신", "소켓 닫기 성공")
        } catch (e: IOException) {
            Log.e("TCP 통신", "소켓 닫기 에러: $e")
        }
    }


    private fun sendMessage(message: String) {

        // 서버에서 데이터를 보내라는 신호를 받아서 플래그가 세워진 경우
        if(isSocket){
            Log.d("TCP 통신", "sendMessage 시작")
            try{
                val data = message.toByteArray() // 데이터를 Byte 배열로 변환
                val b1 = ByteBuffer.allocate(4) // 크기가 4인 ByteBuffer를 생성
                b1.order(ByteOrder.LITTLE_ENDIAN)
                b1.putInt(data.size)
                // 내부 바이트 배열, 배열의 시작 인덱스, 전송할 바이트 수
                outstream!!.write(b1.array(), 0, 4) // 데이터의 길이를 전송
                outstream!!.write(data) // 실제 데이터 전송
                Log.d("TCP 통신", "sendMessage 잘보냄")
            }catch(e : IOException){
                Log.e("TCP 통신", "sendMessage : $e")
            }
        }else{


        }

        // 위는 data와 데이터길이 등을 따로 보내는 방식
        // 아래는 한꺼번에 보내는 방식
        // 한번에 보내면 데이터 크기가 작고, 네트워크 안정적인 경우에 사용. 네트워크 자원 효율성 증가, 코드 복잡성 감소
        // 따로 보내는건 데이터 크키가 크고, 네트워크 불안정적인 경우 사용. 데이터 크기를 먼저 보내서 수신자가 수신할 준비를 하게하고
        // 오류 발생하더라도 다시 시작할 위치를 알려줌
        /*
        val sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.size).array()
        val combined = sizeBuffer + data
        outstream!!.write(combined)
        */
    }

    override fun run() {

        // 서버에서 데이터 보내라는 응답을 대기
        if(!isSocket){

            val createrStart = Thread{
                // 서버에 데이터 보낸다고 요청하기
                try{
                    Log.d("creater receive", "요청 시작")

                    val json = JSONObject()
                    json.put("type", "creater")
                    json.put("action", "update_request")
                    json.put("viewer_on", 0)
                    val send_message = json.toString().toByteArray()
                    val b1 = ByteBuffer.allocate(4)
                    b1.order(ByteOrder.LITTLE_ENDIAN)
                    b1.putInt(send_message.size)
                    outstream!!.write(b1.array(), 0, 4)
                    outstream!!.write(send_message)

                    Log.d("creater receive", "요청 종료")

                }catch(e : Exception){
                    Log.e("creater receive", "요청 : $e")
                }

            }
            createrStart.start()

            Thread{
                createrStart.join()

                // 서버로부터 데이터를 받아 처리하는 코드
                val data2 = ByteArray(4)

                try{
                    Log.d("creater receive", "data2 성공전")
                    instream!!.read(data2, 0, 4)
                    Log.d("creater receive", "data2 성공후")

                }catch(e : Exception){
                    Log.e("creater receive", "찾았다 : $e")
                }

                val b2 = ByteBuffer.wrap(data2)
                b2.order(ByteOrder.LITTLE_ENDIAN)
                val length = b2.int
                val data3 = ByteArray(length)

                try{
                    Log.d("creater receive", "data3 성공전")
                    instream!!.read(data3, 0, length)
                    Log.d("creater receive", "data3 성공후")
                }catch(e : Exception){
                    Log.e("creater receive", "찾았다 : $e")
                }

                val message = String(data3, Charsets.UTF_8)
                val json = JSONObject(message)

                val action_receive = json.getString("action")
                Log.d("creater receive", "$action_receive")

                if(action_receive == "send_data") isSocket = true
            }.start()

        }

    }

}