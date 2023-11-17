package com.yongsu.socketteamproject.viewmodel

import android.os.Handler
import android.util.Log
import com.yongsu.socketteamproject.ShowGameActivity
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ViewerClientThread(private val activity: ShowGameActivity) : Thread() {
    private var socket: Socket? = null
    private var outstream: DataOutputStream? = null
    private var instream: DataInputStream? = null

    init {
        Log.d("엥?", "소켓 생성 전")
        try{
            Log.d("엥?", "소켓 생성 중")
            socket = Socket("192.168.0.3", 8888)
            outstream = DataOutputStream(socket!!.getOutputStream())
            instream = DataInputStream(socket!!.getInputStream())
            Log.d("엥?", "소켓 생성 후")
        }catch(e : Exception){
            Log.e("엥?", "소켓 생성중 : $e")
        }

    }

    fun closeViewerSocket() {
        try {
            val threadKilling = Thread{
                Log.d("엥?", "닫을거에요")
                val json = JSONObject()
                json.put("type", "viewer")
                json.put("action", "viewer_killing")
                val data = json.toString().toByteArray() // 데이터를 Byte 배열로 변환
                val b1 = ByteBuffer.allocate(4) // 크기가 4인 ByteBuffer를 생성
                b1.order(ByteOrder.LITTLE_ENDIAN)
                b1.putInt(data.size)
                // 내부 바이트 배열, 배열의 시작 인덱스, 전송할 바이트 수
                outstream!!.write(b1.array(), 0, 4) // 데이터의 길이를 전송
                outstream!!.write(data) // 실제 데이터 전송
                Log.d("엥?", "닫으라고 보냈어요")
            }

            Thread{
                threadKilling.start()
                threadKilling.join()
                outstream?.close()
                instream?.close()
                socket?.close()

                Log.d("엥?", "소켓 닫기 성공")
            }.start()


        } catch (e: IOException) {
            Log.e("엥?", "소켓 닫기 에러: $e")
        }
    }

    fun sendAndreceive(){
        try {
            Log.d("엥?", "메시지를 내놓으세요")
            val json = JSONObject()
            json.put("type", "viewer")
            json.put("action", "data_request")
            json.put("viewer_on", 1)
            val data = json.toString().toByteArray() // 데이터를 Byte 배열로 변환
            val b1 = ByteBuffer.allocate(4) // 크기가 4인 ByteBuffer를 생성
            b1.order(ByteOrder.LITTLE_ENDIAN)
            b1.putInt(data.size)
            // 내부 바이트 배열, 배열의 시작 인덱스, 전송할 바이트 수
            outstream!!.write(b1.array(), 0, 4) // 데이터의 길이를 전송
            outstream!!.write(data) // 실제 데이터 전송
            Log.d("엥?", "라고 전송했어요")

        }catch(e : Exception){
            Log.e("엥?", "$e")
        }
    }

    fun receiveProcess(){
        val thread = Thread{
            try{

                while(true){

                    //sendAndreceive()

                    // 서버로부터 데이터를 받아 처리하는 코드
                    val data2 = ByteArray(4)

                    try {
                        Log.d("엥?", "data2 성공전")
                        instream!!.read(data2, 0, 4)
                        Log.d("엥?", "data2 성공후")

                    } catch (e: Exception) {
                        Log.e("엥?", "찾았다 : $e")
                    }

                    val b2 = ByteBuffer.wrap(data2)
                    b2.order(ByteOrder.LITTLE_ENDIAN)
                    val length = b2.int
                    val data3 = ByteArray(length)

                    try {
                        Log.d("엥?", "data3 성공전")
                        instream!!.read(data3, 0, length)
                        Log.d("엥?", "data3 성공후")
                    } catch (e: Exception) {
                        Log.e("엥?", "찾았다 : $e")
                    }

                    val message = String(data3, Charsets.UTF_8)
                    val ajson = JSONObject(message)

                    val game_name = ajson.getString("game_name")
                    val team1_name = ajson.getString("team1_name")
                    val team2_name = ajson.getString("team2_name")
                    val team1_score = ajson.getString("team1_score")
                    val team2_score = ajson.getString("team2_score")
                    val sport_type = ajson.getInt("sport_type")
                    val game_half = ajson.getInt("game_half")
                    val game_progress = ajson.getInt("game_progress")

                    Log.d("엥?", "이름: $game_name")

                    //제목, 팀명 업데이트 작업 수행
                    activity.runOnUiThread {
                        activity.receiveMatchInfo(game_name, team1_name, team2_name)
                    }
                    //점수 업데이트 작업 수행
                    activity.runOnUiThread {
                        activity.receiveScore(team1_score, team2_score)
                    }
                    //전후반 업데이트 작업 수행
                    activity.runOnUiThread {
                        activity.receiveHalfStatus(game_half)
                    }

                }

            }catch(e : Exception){
                Log.e("엥?", "받는거 문제 : $e")
            }
        }

        thread.start()
    }

    override fun run() {
        Log.d("엥?", "Thread started")

        Thread{
            sendAndreceive()
            receiveProcess()
        }.start()

        Log.d("엥?", "Thread Ended")
    }
}


//        val type = json.getString("type")
//
//        when (type) {
//            "match_info" -> {
//                val title = json.getString("title")
//                val team1 = json.getString("team1")
//                val team2 = json.getString("team2")
//
//                // 제목, 팀명 업데이트 작업 수행
//                activity.runOnUiThread {
//                    activity.receiveMatchInfo(title, team1, team2)
//                }
//            }
//            "score" -> {
//                val scoreFirst = json.getInt("scoreFirst")
//                val scoreSecond = json.getInt("scoreSecond")
//
//                // 점수 업데이트 작업 수행
//                activity.runOnUiThread {
//                    activity.receiveScore(scoreFirst, scoreSecond)
//                }
//            }
//            "half" -> {
//                val half = json.getBoolean("half")
//
//                // 전후반 업데이트 작업 수행
//                activity.runOnUiThread {
//                    activity.receiveHalfStatus(half)
//                }
//            }
//            "commentary" -> {
//                val comment = json.getString("comment")
//                // 문자 중계 작업 수행
//            }
//        }
