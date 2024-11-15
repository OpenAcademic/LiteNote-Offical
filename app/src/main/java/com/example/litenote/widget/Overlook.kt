package com.example.litenote.widget


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.twotone.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.CItem
import com.example.litenote.entity.Code
import com.example.litenote.utils.expercessToResource
import com.example.litenote.utils.getDarkModeBackgroundColor

import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.bar.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.bar.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.line.LineChart
import me.bytebeats.views.charts.line.LineChartData
import me.bytebeats.views.charts.simpleChartAnimation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.concurrent.thread

@SuppressLint("SimpleDateFormat")
private fun timeStempToTime(timest: Long): String {
    // 转换为 2024-12-12
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date(timest))



}
@Composable
fun HotTable(
    backgroundColor : Color = Color.White,
    subBackgroundColor : Color = Color(0xFFFFFAF8),
    fontColor : Color = Color.Black,
    hots : List<CItem> = listOf(
        CItem("2022-01-01", 1),
        CItem("2022-01-02", 2),
        CItem("2022-01-03", 3),
        CItem("2022-01-04", 4),
        CItem("2022-01-05", 5),
        CItem("2022-01-06", 6),
        CItem("2022-01-07", 7),
        CItem("2022-01-08", 8),
        CItem("2022-01-09", 9),
        CItem("2022-01-10", 10),
        CItem("2022-01-11", 11),
        CItem("2022-01-12", 12),
        CItem("2022-01-13", 13),
        CItem("2022-01-14", 14),
        CItem("2022-01-15", 15),
        CItem("2022-01-16", 16),
        CItem("2022-01-17", 17),
        CItem("2022-01-18", 18),
        CItem("2022-01-19", 19),
        CItem("2022-01-20", 20),
        CItem("2022-01-21", 21),
        CItem("2022-01-22", 22),
        CItem("2022-01-23", 23),
        CItem("2022-01-24", 24),
        CItem("2022-01-25", 25),
        CItem("2022-01-26", 26),
        CItem("2022-01-27", 27),
        CItem("2022-01-28", 28),
        CItem("2022-01-29", 29),
        CItem("2022-01-30", 30),
        CItem("2022-01-31", 31),
        CItem("2022-02-01", 32),
        CItem("2022-02-02", 33),
        CItem("2022-02-03", 34),
        CItem("2022-02-04", 35)
    ),
    columns : Int = 5,
    onclickDots : (str:String) -> Unit = { _ -> }
){
    // 生成最近100天的日期格式
    val dateList = mutableListOf<String>()
    var currTime = System.currentTimeMillis()
    // 获取到第二天凌晨的时间
    currTime = currTime - currTime % (24 * 60 * 60 * 1000) + 24 * 60 * 60 * 1000
    var calendar = Calendar.getInstance()
    calendar.timeInMillis = currTime
    calendar.add(Calendar.DAY_OF_MONTH, -columns)
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH) + 1
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    // 从 100 天前开始

    for (i in 0 until columns) {
        // 补齐 月份和日期前面的 0
        var strMonth = if (month < 10) "0$month" else "$month"
        var strDay = if (day < 10) "0$day" else "$day"

        dateList.add("$year-$strMonth-$strDay")
        calendar.add(Calendar.DAY_OF_MONTH, +1)
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)

    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier

    ) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {

            // 显示
            items(dateList.size) { index ->
                val item = dateList[index]
                // 判断是否在日期列表中
                val isExist = hots.find { it.first == item }?.first != null
                val degree = hots.find { it.first == item }?.second ?: 0
                Icon(painter = painterResource(id = R.drawable.jx),
                    contentDescription = item,
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .clickable {
                            Log.d("sdsad", item)
                            onclickDots(item)
                        },
                    tint = if (isExist) {
                        if (degree > 0 && degree < 3) {
                            Color(0xFF8ddc3d)
                        } else if (degree >= 3 && degree < 6) {
                            Color(0xFF58AF00)
                        } else {
                            Color(0xFF295100)
                        }
                    } else {
                        Color(0xFF8A8A8A)
                    }
                )

            }
        }
    }

}

@Composable
fun MyDialog(
    context: Context,
    drop : () -> Unit = { },
    content : @Composable () -> Unit
){
    Dialog(onDismissRequest = {
        drop()
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                , // 半透明背景
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

                        .zIndex(2f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                // 长按事件
                                onLongPress = {},
                                // 点击事件
                                onTap = {
                                    drop()

                                })
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    content()
                }
            }

        }
    }
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OverPage(
    context: Context,
    backgroundColor : Color = Color.White,
    subBackgroundColor : Color = Color(0xFFFFFAF8),
    fontColor : Color = Color.Black,
    hots : List<CItem> = listOf(
        CItem("2022-01-01", 1),
        CItem("2022-02-04", 35)
    ),
    allNums : List<Int> = listOf(1, 2, 3),
    resources: android.content.res.Resources,

    ){

    val lists = remember {
        mutableStateListOf<Code>()
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(20.dp)

    ) {
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(subBackgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(
                    10.dp
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier

            ) {
                Image(painter = painterResource(id = R.mipmap.all), contentDescription = "all", modifier = Modifier.size(50.dp))
                Text(text = resources.getString(R.string.sum),
                    color = fontColor, fontSize = 12.sp,
                )
                Text(text = ""+ allNums[0] + resources.getString(R.string.jian),
                    color = fontColor, fontSize = 10.sp,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier

            ) {
                Image(painter = painterResource(id = R.mipmap.dqj), contentDescription = "all", modifier = Modifier.size(50.dp))
                Text(text = resources.getString(R.string.daiqu),
                    color = fontColor, fontSize = 12.sp,
                )
                Text(text = ""+ allNums[1] + resources.getString(R.string.jian),
                    color = fontColor, fontSize = 10.sp,
                )
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier

            ){
                Image(painter = painterResource(id = R.mipmap.yqj), contentDescription = "all", modifier = Modifier.size(50.dp))
                Text(text = resources.getString(R.string.yiqu),
                    color = fontColor, fontSize = 12.sp,
                )
                Text(text = ""+ allNums[2] + resources.getString(R.string.jian),
                    color = fontColor, fontSize = 10.sp,
                )
            }

        }
        Spacer(modifier = Modifier.width(20.dp))

        val selet_date = remember {
            mutableStateOf("")
        }
        // LineChart
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp)
                .height(140.dp)
                .background(subBackgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(10.dp),
        ){

            if (hots.isNotEmpty()) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ){
                    HotTable(
                        hots = hots,
                        backgroundColor = backgroundColor,
                        subBackgroundColor = subBackgroundColor,
                        fontColor = fontColor,
                        columns = 60,
                        onclickDots = {
                            lists.clear()
                            val open = CodeDBUtils.getCodesByDay(context = context, strDay = it)
                            Log.d("dfsdf",open.toString())
                            if (open.isNotEmpty()){
                                selet_date.value= it
                                lists.addAll(open)
                            }else{
                                selet_date.value= ""

                                Toast.makeText(context,"当天数据为空",Toast.LENGTH_SHORT).show()
                            }

                        }
                    )
                }

            }
            else{
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Image(painter = painterResource(R.mipmap.empty),
                        contentDescription = "空",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(
                                10.dp
                            )
                    )
                    Text(text = "暂无数据", color = fontColor,
                        modifier = Modifier.padding(
                            10.dp
                        ),
                        fontSize = 20.sp)
                }
            }

        }
        Spacer(modifier = Modifier.width(20.dp))
        if (lists.isNotEmpty()){
            Text(text = selet_date.value, color = fontColor,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
                fontWeight = FontWeight.Bold,
                // 添加删除线
                style = TextStyle(fontStyle = FontStyle.Normal,)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                lists.forEachIndexed { index, code ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                            .background(
                                getDarkModeBackgroundColor(context = context, level = 1),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(10.dp)
                            .clip(RoundedCornerShape(15.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.5f),

                            ) {
                            Image(painter = painterResource(
                                id = expercessToResource(code.kd)
                            ),
                                contentDescription = code.kd,
                                modifier= Modifier
                                    .size(25.dp)
                                    .padding(
                                        end = 6.dp
                                    )
                            )
                            Text(text = code.code, color = fontColor,
                                fontSize = 16.sp,
                                // 添加删除线
                                style = TextStyle(fontStyle = FontStyle.Normal,
                                    textDecoration = if (code.status == 1) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                        }
                        Text(text = code.yz, color = fontColor,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // 添加删除线
                            style = TextStyle(fontStyle = FontStyle.Normal,
                                textDecoration = if (code.status == 1) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )

                    }
                }

            }
        }



    }

}

@Composable
fun EmptyView(
    fontColor: Color
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()

    ) {
        Image(painter = painterResource(R.mipmap.empty),
            contentDescription = "空",
            modifier = Modifier
                .size(100.dp)
                .padding(
                    10.dp
                )
        )
        Text(text = "暂无数据", color = fontColor,
            modifier = Modifier.padding(
                10.dp
            ),
            fontSize = 20.sp)
    }
}