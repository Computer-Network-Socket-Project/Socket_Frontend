package com.yongsu.socketteamproject

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.yongsu.socketteamproject.adapter.GameListAdapter
import com.yongsu.socketteamproject.databinding.ActivityGameSearchResultBinding
import com.yongsu.socketteamproject.retrofit.RetrofitInstance
import com.yongsu.socketteamproject.retrofit.api.GameInterface
import com.yongsu.socketteamproject.retrofit.model.GameInfoRes
import com.yongsu.socketteamproject.viewmodel.GameListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class GameSearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameSearchResultBinding
    val arr = ArrayList<GameInfoRes>()
    private val gson: Gson = Gson()

    private val gameAPI = RetrofitInstance.getInstance().create(GameInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_search_result)

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        initView()
    }

    private fun initView(){
        with(binding){


            lifecycleScope.launch{
                try{
                    Log.d("http통신", "일단 여기까지는")
                    val response = withContext(Dispatchers.IO){
                        gameAPI.getGameList()
                    }
                    Log.d("http통신", "온다는 말인데...")
                    Log.d("http통신", "Response type: ${response::class.java.simpleName}")
                    Log.d("http통신", "${response.isEmpty()}")
                    Log.d("http통신", "${response.size}")

                    // 서버에서 받아온 값들을 모두 arr에 넣어줌
                    arr.addAll(response)

                    val adapter = GameListAdapter(arr)
                    gameRV.adapter = adapter
                    gameRV.layoutManager= LinearLayoutManager(this@GameSearchResultActivity)
                    //gameRV.addItemDecoration(GameListAdapterDecoration())

                    // ShowGameActivity로 데이터 넘기기 (어차피 아이템들이 각기 다른 데이터를 가지고 있으므로 그걸 가져오는게 나을듯 )
                    adapter.setGLClickListener(object: GameListAdapter.GameListClickListener{
                        override fun onGameListTouch(gameListItem: GameInfoRes) {
                            val json = gson.toJson(gameListItem)
                            val intent = Intent(this@GameSearchResultActivity, ShowGameActivity::class.java)
                            intent.putExtra("gameData", json)
                            startActivity(intent)
                        }
                    })
                }catch(e : IOException){
                    Log.e("http통신", "$e")
                }
            }



            floatingBtn.setOnClickListener {
                val intent = Intent(this@GameSearchResultActivity, GameActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    gameAPI.getGameList()
                }
                arr.clear()
                arr.addAll(response)
                binding.gameRV.adapter?.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            } catch (e: IOException) {
                Log.e("http통신", "$e")
            }
        }
    }

//    private fun DummyData() : ArrayList<GameListItem>{
//        val dummy1 = GameListItem(1, "2023년 총장배 농구대회", "라온", "비상",
//            3, 7, "전반전", true, false, 3)
//
//        val dummy2 = GameListItem(2, "2023년 킥오프배 축구대회", "자과", "생과",
//            1, 3, "경기종료", false, true, 2)
//
//        val dummy3 = GameListItem(3, "2023년 킥오프배 축구대회", "컴공", "영문",
//            2, 1, "경기종료", false, true, 7)
//
//        val dummy4 = GameListItem(4, "2023년 바스타즈배 농구대회", "비상A", "비상B",
//            18, 6, "경기종료", false, false, 12)
//
//        val dummy5 = GameListItem(5, "2023년 바스타즈배 농구대회", "생생", "히든",
//            4, 20, "경기종료", false, false, 5)
//
//        val dummy6 = GameListItem(6, "2022년 총장배 축구대회", "교직원", "법정경",
//            2, 5, "경기종료", false, true, 2)
//
//        val arr = ArrayList<GameListItem>()
//        arr.add(dummy1)
//        arr.add(dummy2)
//        arr.add(dummy3)
//        arr.add(dummy4)
//        arr.add(dummy5)
//        arr.add(dummy6)
//
//        return arr
//    }

}

class GameListAdapterDecoration : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.bottom = 20
    }
}