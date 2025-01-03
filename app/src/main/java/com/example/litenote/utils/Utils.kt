/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.glance.GlanceTheme
import androidx.glance.unit.ColorProvider
import com.example.litenote.ProductType
import com.example.litenote.R
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
fun getApplicationStatus(
    context: Context
): Boolean {
    val sharePref = context.getSharedPreferences("init", Context.MODE_PRIVATE)
    return sharePref.getBoolean("isFirst", true)
}


@Composable
fun getProductTypeIcon(type: String)   : Painter = when(type){
    ProductType.PHONE.typeName -> painterResource(id = R.mipmap.phone)
    ProductType.TABLET.typeName -> painterResource(id = R.mipmap.pbdn)
    ProductType.LAPTOP.typeName ->  painterResource(id = R.mipmap.bjbdn)
    ProductType.WEARABLE.typeName -> painterResource(id = R.mipmap.watch)
    ProductType.HEADPHONE.typeName ->  painterResource(id = R.mipmap.ej)
    ProductType.MOUSE.typeName -> painterResource(id = R.mipmap.sb)
    ProductType.SMART_DEVICE.typeName -> painterResource(id = R.mipmap.smartdevice)
    else ->  painterResource(id = R.mipmap.other)
}
fun getApplicationAgentStatus(
    context: Context
): Boolean{
    val sharePref = context.getSharedPreferences("init", Context.MODE_PRIVATE)
    return  sharePref.getBoolean("agent", false)
}

fun timestr2ShowStr(timest: Long): AnnotatedString {
    val sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm");
    val dateStr = sdf.format(java.util.Date(timest).time);
    val strs = dateStr.split("-")
    return  buildAnnotatedString {

        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 20.sp)) {
            append(strs[0])
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 16.sp)) {
            append("年")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 20.sp)) {
            append(strs[1])
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 16.sp)) {
            append("月")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 20.sp)) {
            append(strs[2])
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 16.sp)) {
            append("日")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 20.sp)) {
            append(strs[3])
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 16.sp)) {
            append(":")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 20.sp)) {
            append(strs[4])
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,
            color = Color.Black, fontSize = 18.sp)) {
            append("开")
        }



    }
}

@SuppressLint("SimpleDateFormat")
fun timeStempToTime(timest: Long,mode:Int): String {
    //val ptimest=1000L*timest
    // 转换为 2024-12-12 12:12
    if (mode == 1){
        val sdf =  SimpleDateFormat("yyyy-MM-dd");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }else if (mode == 2){
        val sdf =  SimpleDateFormat("yyyy-MM");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }else if (mode == 3){
        val sdf =  SimpleDateFormat("yyyy");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }

    else{
        val sdf =  SimpleDateFormat("yyyy-MM-dd HH:mm");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }

}
fun expercessToResource(
    name: String
):Int{
    // /home/wxd2zrx/下载/def.png  未知
    ///home/wxd2zrx/下载/zto.png   中通
    ///home/wxd2zrx/下载/yto.png  圆通
    ///home/wxd2zrx/下载/bs.png  百世
    ///home/wxd2zrx/下载/sto.png  申通
    ///home/wxd2zrx/下载/yz.png  邮政
    ///home/wxd2zrx/下载/db.png  德邦
    ///home/wxd2zrx/下载/yd.png  韵达
    ///home/wxd2zrx/下载/jt.png  极兔
    ///home/wxd2zrx/下载/jd.png  京东
    ///home/wxd2zrx/下载/sf.png   顺丰
    return when(name){
        "中通" -> R.mipmap.zto
        "圆通" -> R.mipmap.yto
        "百世" -> R.mipmap.bs
        "申通" -> R.mipmap.sto
        "邮政" -> R.mipmap.yz
        "德邦" -> R.mipmap.db
        "韵达" -> R.mipmap.yd
        "极兔" -> R.mipmap.jt
        "京东" -> R.mipmap.jd
        "顺丰" -> R.mipmap.sf
        "汇通" -> R.mipmap.bs
        else -> R.mipmap.def
    }
}
enum class ModeType(val value: Int,var show:String) {
    AUTO(0,"自动"),
    LIGHT(1, "浅色模式"),
    NIGHT(2 , "深色模式"),
}
fun getModeType(value: Int): ModeType {
    return when (value) {
        0 -> ModeType.AUTO
        1 -> ModeType.LIGHT
        2 -> ModeType.NIGHT
        else -> ModeType.AUTO
    }
}


fun isDarkMode(
    context: Context
): Boolean {
    val mode = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
    val modelType = ConfigUtils.getDarkModeType(context)
    if (modelType == ModeType.AUTO){
        return mode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }else if (modelType == ModeType.LIGHT){
        return false
    } else if (modelType == ModeType.NIGHT){
        return true
    } else {
        return mode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
fun isDarkMode2(
    context: Context
): Boolean {
    val mode = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
    val modelType = ConfigUtils.getDarkModeType(context)
    if (modelType == ModeType.AUTO){
        return mode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }else if (modelType == ModeType.LIGHT){
        return false
    } else if (modelType == ModeType.NIGHT){
        return true
    } else {
        return mode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
fun getDarkModeTextColor2(
    context: Context
): Color {
    return if (isDarkMode2(context)) {
        Color.White
    } else {
        Color.Black
    }
}
fun getDarkModeTextColor(
    context: Context
): Color {
    return if (isDarkMode(context)) {
        Color.White
    } else {
        Color.Black
    }
}

fun getBtnDarkModeTextColor(
    context: Context
): Color {
    return if (!isDarkMode(context)) {
        Color.White
    } else {
        Color.Black
    }
}

fun randomColor(): Color {
    val r = (0..255).random()
    val g = (0..255).random()
    val b = (0..255).random()
    return Color(r, g, b)
}

@Composable
fun getDarkModeBackgroundColor(
    context: Context,
    level : Int
): Color {
    return if (isDarkMode(context)) {
        if (level == 0) {
            MaterialTheme.colorScheme.surface
        } else if (level == 1) {
            MaterialTheme.colorScheme.surfaceContainer
        } else if (level == 2) {
            Color.Gray
        } else {
            Color.Black
        }
    } else {
        if (level == 0) {
            MaterialTheme.colorScheme.surface
        } else if (level == 1) {
            MaterialTheme.colorScheme.surfaceContainer
        } else if (level == 2) {
            Color(0xFFFFF8E3)
        } else {
            Color.White
        }
    }
}



@Composable
fun getWidgetDarkModeBackgroundColor(
    context: Context,
    level : Int
): ColorProvider {
    return if (isDarkMode(context)) {
        if (level == 0) {
            GlanceTheme.colors.surface
        } else if (level == 1) {
            GlanceTheme.colors.secondary
        } else if (level == 2) {
            ColorProvider(Color.Gray)
        } else {
            ColorProvider(Color.Black)
        }
    } else {
        if (level == 0) {
            GlanceTheme.colors.onSurface
        } else if (level == 1) {
            GlanceTheme.colors.onSecondaryContainer
        } else if (level == 2) {
            ColorProvider(Color(0xFFFFF8E3))
        } else {
            ColorProvider(Color.White)
        }
    }
}

@Composable
fun getWidgetDarkModeBackgroundColor2(
    context: Context,
    level : Int
): ColorProvider {
    return if (isDarkMode2(context)) {
        if (level == 0) {
            GlanceTheme.colors.surface
        } else if (level == 1) {
            GlanceTheme.colors.secondaryContainer
        } else if (level == 2) {
            ColorProvider(Color.Gray)
        } else {
            ColorProvider(Color.Black)
        }
    } else {
        if (level == 0) {
            GlanceTheme.colors.surface
        } else if (level == 1) {
            GlanceTheme.colors.secondaryContainer
        } else if (level == 2) {
            ColorProvider(Color(0xFFFFF8E3))
        } else {
            ColorProvider(Color.White)
        }
    }
}

fun daysToYearDays(totalDays: Int): String {
    val years = totalDays / 365
    val days = totalDays % 365
    return when {
        years > 0 && days > 0 -> "${years}年${days}天"
        years > 0 -> "${years}年"
        else -> "${days}天"
    }
}