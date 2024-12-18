/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.utils

import android.content.Context
import com.example.litenote.UpdateInfo
import com.example.litenote.utils.HomeStyle.CARD
import com.example.litenote.utils.HomeStyle.LIST
import com.google.gson.Gson

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
    ;
} 
fun getHomeType(value: Int): HomeType {
    return when (value) {
        0 -> HomeType.NOTE
        1 -> HomeType.CODE
        3 -> HomeType.PRODUCT
        4 -> HomeType.TRAIN_TICKET
        else -> HomeType.CODE
    }
}

object ConfigUtils {
    fun checkSwitchConfig(context: Context,name:String):Boolean{
        val sharePref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val  isDisableDefault = sharePref.getBoolean(name, false)
        return isDisableDefault
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