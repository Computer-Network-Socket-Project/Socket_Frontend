package com.yongsu.socketteamproject

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yongsu.socketteamproject.adapter.GameListAdapter
import com.yongsu.socketteamproject.databinding.ActivityGameSearchResultBinding
import com.yongsu.socketteamproject.viewmodel.GameListItem

class GameSearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameSearchResultBinding
    private val adapter = GameListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_search_result)

        initView()
    }

    private fun initView(){
        with(binding){
            gameRV.adapter = adapter
            gameRV.layoutManager= LinearLayoutManager(this@GameSearchResultActivity)
            //gameRV.addItemDecoration(GameListAdapterDecoration())
            adapter.submitList(DummyDate())

            floatingBtn.setOnClickListener {
                val intent = Intent(this@GameSearchResultActivity, GameActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun DummyDate() : ArrayList<GameListItem>{
        val dummy1 = GameListItem(1, "2023년 총장배 농구대회", "라온", "비상",
            3, 7, "전반전", true, false, 3)

        val dummy2 = GameListItem(2, "2023년 킥오프배 축구대회", "자과", "생과",
            1, 3, "경기종료", false, true, 2)

        val dummy3 = GameListItem(3, "2023년 킥오프배 축구대회", "컴공", "영문",
            2, 1, "경기종료", false, true, 7)

        val dummy4 = GameListItem(4, "2023년 바스타즈배 농구대회", "비상A", "비상B",
            18, 6, "경기종료", false, false, 12)

        val dummy5 = GameListItem(5, "2023년 바스타즈배 농구대회", "생생", "히든",
            4, 20, "경기종료", false, false, 5)

        val dummy6 = GameListItem(6, "2022년 총장배 축구대회", "교직원", "법정경",
            2, 5, "경기종료", false, true, 2)

        val arr = ArrayList<GameListItem>()
        arr.add(dummy1)
        arr.add(dummy2)
        arr.add(dummy3)
        arr.add(dummy4)
        arr.add(dummy5)
        arr.add(dummy6)

        return arr
    }

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