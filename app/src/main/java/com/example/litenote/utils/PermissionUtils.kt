package com.example.litenote.utils

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionUtils {
    //     <uses-permission android:name="android.permission.RECEIVE_SMS" /> <!-- 发送消息 -->
    //    <uses-permission android:name="android.permission.SEND_SMS" /> <!-- 阅读消息 -->
    //    <uses-permission android:name="android.permission.READ_SMS" /> <!-- 写入消息 -->
    //    <uses-permission android:name="android.permission.WRITE_SMS" />

    val permissions = listOf(
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_SMS",
        "android.permission.POST_NOTIFICATIONS",

    )
    var permissions_usages = arrayOf(
        "短信读取权限",
        "通知发送权限",
        "后台服务权限",
        "悬浮窗权限"
    )
    var permissions_descriptions = arrayOf(
        "用于读取短信以提取其中的取件码信息，便于存储于数据库之中。",
        "用于发送通知，以提示服务已经开启。",
        "用于后台服务，便于在后台也能够监听短信。",
        "对于非HyperOS系统，用于显示悬浮窗，以便于在锁屏界面上显示取件码信息。"
    )
    fun checkShowOnLockScreen(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!checkPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    fun requestPermissions(context: Context, permissions: Array<String>, requestCode: Int) {
        if (!checkPermissions(context, permissions)) {
            // 请求权限


        }
    }

}