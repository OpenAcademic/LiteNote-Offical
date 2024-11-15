package com.example.litenote.utils

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