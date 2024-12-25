package com.example.litenote.utils

import android.content.Context
import com.example.litenote.entity.HuiLvEntity
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object HuiluObj {
    fun getLastHUILu(
        context: Context
    ) {
        // 请求 https://api.exchangerate-api.com/v4/latest/ 获取汇率
        // 获取汇率后，根据汇率计算出最新的汇率
        // 使用 OKHttp 请求
        // 请求成功后，使用 Gson 解析数据
        // 获取汇率
        // 读取目录下的文件
        val client = OkHttpClient() // 创建一个okhttp客户端对象

        // 创建一个GET方式的请求结构
        val request: Request = Request.Builder().url(
            "https://api.exchangerate-api.com/v4/latest/" + ConfigUtils.getCostType(context).symbol2
        ).build()
        val call = client.newCall(request) // 根据请求结构创建调用对象
        call.enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string() // 获取响应数据
                val obj = Gson().fromJson(body, HuiLvEntity::class.java)
                val rate = obj.rates
                if (rate != null) {
                    // 保存到 缓存
                    ConfigUtils.saveLastHuiLu(context, Gson().toJson(rate))
                }
                println("onResponse: $body")
            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("onFailure: $e")
            }

        })


    }
}