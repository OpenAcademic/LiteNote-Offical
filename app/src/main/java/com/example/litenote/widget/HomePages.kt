package com.example.litenote.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.CItem
import com.example.litenote.entity.Code
import com.example.litenote.sub.CenterButton
import com.example.litenote.sub.LeftButton
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.HomeStyle
import com.example.litenote.utils.expercessToResource
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import java.text.SimpleDateFormat
import java.util.Date

val status = listOf(
    R.string.all,
    R.string.uncollected,
    R.string.collected
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeStatus(
    context: Context,
    currTag: Int,
    resources: android.content.res.Resources,
    onTagChange: (Int) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (index in status.indices) {
            val leadingIcon: @Composable () -> Unit = {
                Icon(Icons.Default.Check, null) }
            FilterChip(
                selected = index == currTag,
                onClick = {
                    onTagChange(index)
                },
                label = { Text(resources.getString(status[index])) },
                leadingIcon = if (index == currTag) leadingIcon else null
            )
        }

    }

}
private fun getDayChange(timest: Long): String {
    // 获取当前时间戳
    val currentTime = System.currentTimeMillis()
    // 计算时间差
    val time = currentTime - timest
    // 计算天数
    val day = time / (1000 * 60 * 60 * 24)
    // 计算小时数
    if (day > 0) {
        return "${day}天前"
    }else{
        val hour = time / (1000 * 60 * 60)
        if (hour > 0) {
            return "${hour}小时前"
        }else{
            val minute = time / (1000 * 60)
            if (minute > 0) {
                return "${minute}分钟前"
            }else{
                return "刚刚"
            }
        }
    }

}


@SuppressLint("SimpleDateFormat")
private fun timeStempToTime(timest: Long): String {
    //val ptimest=1000L*timest
    // 转换为 2024-12-12 12:12
    val sdf =  SimpleDateFormat("yyyy-MM-dd");
    val dateStr = sdf.format(Date(timest).time);
    return dateStr;
}
data class HomePortObj(
    val first:String,
    val second:Int,
    var yzLocal : String?,

    val lists:SnapshotStateList<Code>
)
@SuppressLint("RememberReturnType")
@Composable
fun CodeCard(
    modifier: Modifier = Modifier,
    fontColor : Color = Color.Black,
    card : Code = Code(1, "08-98-1878","","",0,0,0,1718882771000, "","",""),
    onClickDots: (yid:Int,id:Long,currStatus:Int) -> Unit = { _,_,_ -> },
    index:  Int = 0,
    portindex : Int = 0,
    onDeleteDots: (yid:Int,id:Long,currStatus:Int) -> Unit = { _,_,_ -> },
    onEditCode:  (code:Code) -> Unit = { _ -> },
    onLongClicked: (code:Code) -> Unit
    ){
    var checked = remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                Log.d("dragAmount", "dragAmount: $it")
                onLongClicked(card)
            })
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(5.dp, 0.dp)
        ) {

            RadioButton(
                selected = card.status == 1 ,
                onClick = {
                    onClickDots(portindex,card.id,card.status)

                },
                modifier = Modifier
                    .fillMaxWidth(0.1f)

            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier= Modifier.fillMaxWidth(0.79f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,

                        ){
                        Image(painter = painterResource(
                            id = expercessToResource(card.kd)),
                            contentDescription = card.kd,
                            modifier= Modifier
                                .size(30.dp)
                                .padding(
                                    end = 6.dp
                                )
                        )
                        Column {
                            Row {
                                var showText = if (card.kd.contains("快递")) {
                                    card.kd
                                }  else  if (card.kd.contains("驿站")) {
                                    card.kd
                                }
                                else {
                                    card.kd+"快递"
                                }
                                Text(text = showText+" ", color = fontColor,
                                    fontSize = 16.sp,
                                    // 添加删除线
                                    style = TextStyle(fontStyle = FontStyle.Normal,
                                        textDecoration = if (card.status == 1) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                )

                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = card.code+" ",
                                    color = fontColor, fontSize = 16.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    style = TextStyle(fontStyle = FontStyle.Normal,
                                        textDecoration = if (card.status == 1) TextDecoration.LineThrough else TextDecoration.None
                                    )

                                )
                            }
                            Text(text = "到站日期："+timeStempToTime(card.insertTime),
                                color = fontColor, fontSize = 12.sp,
                                modifier=Modifier,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                style = TextStyle(fontStyle = FontStyle.Normal,
                                    textDecoration = if (card.status == 1) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                        }

                    }
                }




                Row(
                    modifier= Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = getDayChange(card.insertTime),
                        color = fontColor, fontSize = 16.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        style = TextStyle(fontStyle = FontStyle.Normal,
                            textDecoration = if (card.status == 1) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                }
            }





        }

    }
}
@Composable
fun HomePages(
    context: Context,
    currTag:Int=0,
    resources: android.content.res.Resources,
    onTagChange: (Int) -> Unit,
    ports : SnapshotStateList<HomePortObj> = mutableStateListOf(),
    onReflesh: () -> Unit,
    onNextPage: (Int,Int) -> Boolean,
    onLongClicked: (code: Code) -> Unit
) {
    var checked = remember { mutableStateOf(false) }
    val homeStyle = remember {
        mutableStateOf(ConfigUtils.checkHomeStyleConfig(context,"home_style"))
    }

    Column(
        modifier = if (homeStyle.value == HomeStyle.LIST)
            Modifier
                .fillMaxSize()
                .padding(15.dp)
                .background(
                    getDarkModeBackgroundColor(context = context, level = 0),
                )
                .verticalScroll(
                    rememberScrollState()
                )
        else
            Modifier
                .fillMaxSize()
                .padding(15.dp)
                .background(
                    getDarkModeBackgroundColor(context = context, level = 0),
                )
            ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        TextButton(
            onClick = {
                Toast.makeText(context, resources.getString(
                    R.string.demo_ts
                ), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    getDarkModeBackgroundColor(context = context, level = 1),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(
                    2.dp
                )
        ) {
            MarqueeText(
                text = resources.getString(
                    R.string.ts
                ),
                color = getDarkModeTextColor(context = context),
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold

            )


        }
        HomeStatus(context, currTag, resources) {
            onTagChange(it)
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (homeStyle.value == HomeStyle.LIST){
            ports.forEachIndexed { portindex, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    var currpage = remember {
                        mutableStateOf(1)
                    }
                    var onOpen = remember {
                        mutableStateOf(true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (item.first == "未知驿站"){
                                    resources.getString(R.string.unknown)
                                } else{
                                    item.first
                                },
                                color = getDarkModeTextColor(context),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (item.yzLocal==null){
                                    resources.getString(R.string.unknown_local)
                                } else{
                                    item.yzLocal.toString()
                                },
                                color = getDarkModeTextColor(context),
                                modifier = Modifier.fillMaxWidth(0.8f),

                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = resources.getString(R.string.kds)+":"+item.second.toString(),
                                color = getDarkModeTextColor(context),
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(0.8f),

                                fontWeight = FontWeight.Normal
                            )
                        }
                        TextButton(onClick = {
                            onOpen.value = !onOpen.value
                        }) {
                            Icon(
                                imageVector = if (onOpen.value) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = "open",
                                tint = getDarkModeTextColor(context),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    AnimatedVisibility(visible = onOpen.value) {
                        Column(
                            modifier = Modifier.background(
                                getDarkModeBackgroundColor(context = context, level = 1),
                                shape = MaterialTheme.shapes.large
                            )
                        ) {

                            // fontColor : Color = Color.Black,
                            //    card : Code = Code(1, "08-98-1878","","",0,0,0,1718882771000, "","",""),
                            //    onClickDots: (yid:Int,id:Int,currStatus:Int) -> Unit = { _,_,_ -> },
                            //    index:  Int = 0,
                            //    portindex : Int = 0,
                            //    onDeleteDots: (yid:Int,id:Int,currStatus:Int) -> Unit = { _,_,_ -> },
                            //    onEditCode:  (code:Code) -> Unit = { _ -> },
                            item.lists.forEachIndexed { itemlist, code ->
                                CodeCard(
                                    card = code,
                                    index = itemlist,
                                    portindex = portindex,
                                    onEditCode = {
                                        onTagChange(1)
                                        onReflesh()
                                    },
                                    onDeleteDots = { yid, id, currStatus ->
                                        CodeDBUtils.delete(context,id.toLong())
                                        onReflesh()
                                    }
                                    ,
                                    onClickDots = { yid, id, currStatus ->
                                        Log.d("MainActivity",id.toString())
                                        CodeDBUtils.complete(context,id.toLong())
                                        onReflesh()

                                    }
                                    ,
                                    fontColor = getDarkModeTextColor(context = context),
                                    onLongClicked = {
                                        onLongClicked(it)


                                    }

                                )

                            }
                            if(item.second > item.lists.size) {
                                TextButton(
                                    modifier = Modifier,
                                    onClick = {
                                        currpage.value += 1
                                        val isNext = onNextPage(portindex,currpage.value)
                                        Log.d("MainActivity",isNext.toString())

                                        if (!isNext){
                                            currpage.value -= 1

                                        }

                                    }) {
                                    Text(text = resources.getString(R.string.more),
                                        color = getDarkModeTextColor(context),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else{
            val portindex = remember {
                mutableStateOf(0)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                RotationCard(context = context,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    item = ports[portindex.value], fontColor = getDarkModeTextColor(context),
                    backgroundColor = getDarkModeBackgroundColor(context = context, level = 1),
                    portindex = portindex.value,
                    onNextPage = {pi,cp->
                        onNextPage(pi,cp)
                    },
                    onReflesh = onReflesh,
                    onLongClicked = onLongClicked,
                    onTagChange = onTagChange,

                    )
                CenterButton(context = context, text = resources.getString(
                    R.string.next_port
                ),
                    icon = Icons.Default.ArrowForward) {
                    portindex.value = (portindex.value+1)%ports.size
                }

            }
        }

    }
}