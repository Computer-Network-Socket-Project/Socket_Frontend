package com.yongsu.socketteamproject.viewmodel

import android.os.Handler
import com.yongsu.socketteamproject.ShowGameActivity
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ViewerClientThread(private val activity: ShowGameActivity) : Thread() {
    private var socket: Socket? = null
    private var outstream: DataOutputStream? = null
    private var instream: DataInputStream? = null

    init {
        socket = Socket("192.168.44.218", 9999)
        outstream = DataOutputStream(socket!!.getOutputStream())
        instream = DataInputStream(socket!!.getInputStream())
    }

    override fun run() {
        // 서버로부터 데이터를 받아 처리하는 코드
        val data2 = ByteArray(4)
        instream!!.read(data2, 0, 4)
        val b2 = ByteBuffer.wrap(data2)
        b2.order(ByteOrder.LITTLE_ENDIAN)
        val length = b2.int
        val data3 = ByteArray(length)
        instream!!.read(data3, 0, length)

        val message = String(data3, Charsets.UTF_8)
        val json = JSONObject(message)
        val type = json.getString("type")

        when (type) {
            "match_info" -> {
                val title = json.getString("title")
                val team1 = json.getString("team1")
                val team2 = json.getString("team2")

                // 제목, 팀명 업데이트 작업 수행
                activity.runOnUiThread {
                    activity.receiveMatchInfo(title, team1, team2)
                }
            }
            "score" -> {
                val scoreFirst = json.getInt("scoreFirst")
                val scoreSecond = json.getInt("scoreSecond")

                // 점수 업데이트 작업 수행
                activity.runOnUiThread {
                    activity.receiveScore(scoreFirst, scoreSecond)
                }
            }
            "half" -> {
                val half = json.getBoolean("half")

                // 전후반 업데이트 작업 수행
                activity.runOnUiThread {
                    activity.receiveHalfStatus(half)
                }
            }
            "commentary" -> {
                val comment = json.getString("comment")
                // 문자 중계 작업 수행
            }
        }
    }
}