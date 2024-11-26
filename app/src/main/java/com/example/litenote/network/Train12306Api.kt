package com.example.litenote.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class TrainParams(
    val date: String,
    val station_train_code: String,
    val train_no: String,
    val total_num: String,
    val from_station: String,
    val to_station: String
)
       
data class Train12306Response(
    val data: List<Train12306Data>,
    val status: Boolean,
    val errorMsg: String
)

data class Train12306Data(
    val params: TrainParams
)

data class TrainStationInfo(
    val station_name: String,        // 站名
    val arrive_time: String,         // 到达时间
    val start_time: String,          // 发车时间
    val arrive_day_str: String,      // 到达日期描述
    val arrive_day_diff: String,     // 到达日差异
    val station_no: String,          // 站序
    val running_time: String         // 运行时间
)

data class TrainTimeResponse(
    val status: Boolean,
    val httpstatus: Int,
    val data: TrainTimeData
)

data class TrainTimeData(
    val data: List<TrainStationInfo>
)

object Train12306Api {
    private const val BASE_URL = "https://search.12306.cn/search/v1/h5/search"
    private val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun queryTrain(trainCode: String): TrainParams? {
        try {
            val url = "$BASE_URL?keyword=$trainCode&suorce=&action=&_=${System.currentTimeMillis()}"
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val jsonStr = response.body?.string() ?: return null
            
            val gson = Gson()
            val trainResponse = gson.fromJson(jsonStr, Train12306Response::class.java)
            
            if (!trainResponse.status || trainResponse.data.isEmpty()) {
                return null
            }
            
            return trainResponse.data.firstOrNull()?.params
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun queryTrainTime(trainNo: String, date: String): List<TrainStationInfo>? {
        try {
            val url = "https://kyfw.12306.cn/otn/queryTrainInfo/query?" +
                     "leftTicketDTO.train_no=$trainNo&" +
                     "leftTicketDTO.train_date=$date&" +
                     "rand_code="
            
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return null
            }
            
            val jsonStr = response.body?.string() ?: return null
            val gson = Gson()
            val trainResponse = gson.fromJson(jsonStr, TrainTimeResponse::class.java)
            
            if (!trainResponse.status || trainResponse.httpstatus != 200) {
                return null
            }
            
            return trainResponse.data.data
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
} 