package kr.ac.kumoh.s20190703.prof.w1102volleywithviewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class SongViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "SongVolleyRequest" //어떤걸 취소시킬지 옷사면 태그 있는거
    }
    private val list = ArrayList<String>()
    val songs = MutableLiveData<ArrayList<String>>()

    //queue는 init에서 초기화하므로 lateinit 필요 없음
    private var queue: RequestQueue = Volley.newRequestQueue(getApplication())

    init {
        songs.value = list
    }

    fun requestSong() {
        val url = "https://expresssongdb-qnwoh.run.goorm.io/song" //goorm 서버 연동
        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {
                list.clear()
                parseJson(it)
                songs.value = list
                Toast.makeText(getApplication(),
                    list.toString(), //list.toString()으로 수정
                    Toast.LENGTH_LONG).show()
            },//성공했을 때
            {
                Toast.makeText(getApplication(),
                    it.toString(),
                    Toast.LENGTH_LONG).show()
            } //실패했을 때
        )
        request.tag = QUEUE_TAG
        queue.add(request)
    }

    private fun parseJson(items : JSONArray) {
        for ( i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val title = item.getString("title")
            val singer = item.getString("singer")
            list.add("$title ($singer)")
        }
    }

    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG) //tag 에 해당하는 것을 전부 삭제해라
    }
}