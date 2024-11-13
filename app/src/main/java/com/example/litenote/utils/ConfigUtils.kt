package com.example.litenote.utils

import android.content.Context

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
}