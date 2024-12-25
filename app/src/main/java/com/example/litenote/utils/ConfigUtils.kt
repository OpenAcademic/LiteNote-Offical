/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.utils

import android.content.Context
import android.widget.Toast
import com.example.litenote.UpdateInfo
import com.example.litenote.entity.HuiLvEntity
import com.example.litenote.entity.Rates
import com.example.litenote.entity.SubmitCostType
import com.example.litenote.entity.getSubmitCostTypeByInt
import com.example.litenote.utils.HomeStyle.CARD
import com.example.litenote.utils.HomeStyle.LIST
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

enum class HomeStyle(val value: Int) {
    LIST(0),
    CARD(1),


}
fun getHomeStyle(value: Int): HomeStyle {
    return when (value) {
        0 -> LIST
        1 -> CARD
        else -> LIST
    }
}
fun getHomeStyleByShow(show: String): HomeStyle {
    return when (show) {
        "ERNIE-Speed 8K" -> LIST
        "ERNIE-Speed-Pro 128K" -> CARD
        else -> LIST
    }
}

enum class ModelList(val value: Int,  val show: String) {
    ERNIESpeed8K(0, "ERNIE-Speed 8K"),
    ERNIESpeedPro(1, "ERNIE-Speed-Pro 128K"),
}
fun getModelList(value: Int): ModelList {
    return when (value) {
        0 -> ModelList.ERNIESpeed8K
        1 -> ModelList.ERNIESpeedPro
        else -> ModelList.ERNIESpeed8K
    }
}

enum class HomeType(val value: Int) {
    CODE(1),
    NOTE(0),
    PRODUCT(3),
    TRAIN_TICKET(4),
    SUBSCIPTION(6),
    ;
} 
fun getHomeType(value: Int): HomeType {
    return when (value) {
        0 -> HomeType.NOTE
        1 -> HomeType.CODE
        3 -> HomeType.PRODUCT
        4 -> HomeType.TRAIN_TICKET
        6 -> HomeType.SUBSCIPTION
        else -> HomeType.CODE
    }
}

object ConfigUtils {
    fun checkSwitchConfig(context: Context,name:String):Boolean{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getBoolean(name, false)
        return isDisableDefault
    }
    fun saveLastHuiLu(context: Context,value:String){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("lastHuiLu", value)
        editor.apply()
    }
    private fun getLastHUILu(
        context: Context,
        symbol2: String = "CNY"
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
            "https://api.exchangerate-api.com/v4/latest/" + symbol2
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
    fun getLastHuiLu(context: Context):Rates?{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getString("lastHuiLu","")
        return if (isDisableDefault.isNullOrEmpty()) null else Gson().fromJson(isDisableDefault, Rates::class.java)
    }
    fun setSwitchConfig(context: Context,name:String,value:Boolean){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }
    fun getDarkModeType(context: Context):ModeType{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt("dark", 0)
        return getModeType(isDisableDefault)
    }
    fun setDarkModeType(context: Context,value:ModeType){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putInt("dark", value.value)
        editor.apply()
    }
    fun getHomeTypeNum(context: Context):Int{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt("homeType", 1)
        return isDisableDefault
    }

    fun setCostType(context: Context,value:SubmitCostType){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putInt("cost", value.type)
        editor.apply()
        ConfigUtils.saveLastHuiLu(context, "")

        getLastHUILu(context, value.symbol2)
    }
    fun getCostType(context: Context):SubmitCostType{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt("cost", 0)
        return getSubmitCostTypeByInt(isDisableDefault)

    }
    fun checkHomeTypeConfig(context: Context):HomeType{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt("homeType", 1)
        return getHomeType(isDisableDefault)
    }
    fun setHomeTypeConfig(context: Context,value:HomeType){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putInt("homeType", value.value)
        editor.apply()
    }
    fun checkHomeStyleConfig(context: Context,name:String):HomeStyle{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt(name, 0)
        return getHomeStyle(isDisableDefault)
    }
    fun setHomeStyleConfig(context: Context,name:String,value:HomeStyle){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putInt(name, value.value)
        editor.apply()
    }

    fun checkModelListConfig(context: Context):ModelList{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getInt("model", 0)
        return getModelList(isDisableDefault)
    }

    fun setModelListConfig(context: Context,value:ModelList){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putInt("model", value.value)
        editor.apply()
    }

    fun checkDownloadProcess(context: Context,name:String):Long{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getLong(name, 0)
        return isDisableDefault
    }
    fun setDownloadProcessg(context: Context,name:String,value:Long){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putLong(name, value)
        editor.apply()
    }

    fun getUpdateInfoProcessg(context: Context):UpdateInfo?{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getString("update", "")
        try {
            if (isDisableDefault.isNullOrEmpty()) {
                return null
            } else {
                return Gson().fromJson(isDisableDefault, UpdateInfo::class.java)
            }

        } catch (e: Exception) {
            return null
        }
    }
    fun setUpdateInfoProcessg(context: Context,obj:UpdateInfo){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("update", Gson().toJson(obj))
        editor.apply()
    }
    fun cleanUpdateInfoProcessg(context: Context){
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("update", "")
        editor.apply()
    }

}